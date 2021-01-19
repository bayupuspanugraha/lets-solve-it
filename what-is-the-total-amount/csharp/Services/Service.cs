using csharp.Models;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Globalization;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Threading.Tasks;

namespace csharp.Services
{
    public class Service
    {
        static readonly HttpClient client = new HttpClient();

        private async Task<Response> GetDataFromUrl(Int32 userId, Int32 page)
        {
            try
            {
                var uriBldr = new UriBuilder(new Uri($"https://jsonmock.hackerrank.com/api/transactions/search?userId={userId}&page={page}"));
                var request = new HttpRequestMessage
                {
                    Method = HttpMethod.Get,
                    RequestUri = uriBldr.Uri
                };

                request.Headers.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));

                var httpResponse = await client.SendAsync(request);
                var statusCode = (int)httpResponse.StatusCode;

                var responseBody = await httpResponse.Content.ReadAsStringAsync();
                var jsonResponse = JObject.Parse(responseBody);
                var response = JsonConvert.DeserializeObject<Response>(responseBody);

                return response;
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message + ": inner exception: " + ex.InnerException + ": stack trace: " + ex.StackTrace);
            }

            return null;
        }

        private Int32 ConvertAmount(string money)
        {
            var sanitiseMoney = money.Replace("$", "").Replace(",", ""); 
            var moneyInFloat = Convert.ToDecimal(sanitiseMoney, new CultureInfo("en-US"));

            return (Int32)Math.Round(moneyInFloat, 0);
        }

        private Int32 CalculateAmount(IList<Data> datas, Int32 locationId, Int32 netStart, Int32 netEnd)
        {
            var total = 0;
            foreach (var data in datas)
            {
                if (locationId == data.Location.Id)
                {
                    var ips = data.Ip.Split(".");
                    var firstData = int.Parse(ips[0]);

                    if (firstData >= netStart && firstData <= netEnd)
                    {
                        total += ConvertAmount(data.Amount);
                    }
                }
            }

            return total;
        }

        private async Task<Tuple<Response, Int32>> HandleRequest(Int32 userId, Int32 locationId, Int32 netStart, Int32 netEnd, Int32 page)
        {
            var resp = await GetDataFromUrl(userId, page);
            var total = 0;
            if (resp.Total > 0)
            {
                total += CalculateAmount(resp.DataItem, locationId, netStart, netEnd);
            }

            return Tuple.Create(resp, total);
        }

        public async Task<Int32> GetTransactionsAsync(Int32 userId, Int32 locationId, Int32 netStart, Int32 netEnd)
        {
            Stopwatch stopwatch = new Stopwatch();
            stopwatch.Start();

            var response = await HandleRequest(userId, locationId, netStart, netEnd, 1);

            var resp = response.Item1;
            var total = response.Item2;

            if (resp.TotalPages > 1)
            {
                // Sequential version
                for (var page = 2; page <= resp.TotalPages; page++)
                {
                    var temp = await HandleRequest(userId, locationId, netStart, netEnd, page);
                    total += temp.Item2; 
                }
            }

            stopwatch.Stop();
            Console.WriteLine("Elapsed Time is {0} ms", stopwatch.ElapsedMilliseconds);

            return total;
        }

        public async Task<Int32> GetTransactionsParallelAsync(Int32 userId, Int32 locationId, Int32 netStart, Int32 netEnd)
        {
            Stopwatch stopwatch = new Stopwatch();
            stopwatch.Start();

            var response = await HandleRequest(userId, locationId, netStart, netEnd, 1);

            var resp = response.Item1;
            var total = response.Item2;

            if (resp.TotalPages > 1)
            {               
                // Parallel Version
                var tasks = new List<Task<Tuple<Response, Int32>>>();
                for (var page = 2; page <= resp.TotalPages; page++)
                {
                    tasks.Add(HandleRequest(userId, locationId, netStart, netEnd, page));
                }

                await Task.WhenAll(tasks);

                foreach (var item in tasks)
                {
                    var temp = item.Result;
                    total += temp.Item2;
                }
            }

            stopwatch.Stop();
            Console.WriteLine("Elapsed Time is {0} ms", stopwatch.ElapsedMilliseconds);

            return total;
        }
    }
}
