using System;
using System.Collections.Generic;
using Newtonsoft.Json; 

namespace csharp.Models
{

    public class Response
    {
        [JsonProperty("page")]
        public object Page { get; set; }

        [JsonProperty("per_page")]
        public Int32 PerPage { get; set; }

        [JsonProperty("Total")]
        public Int32 Total { get; set; }

        [JsonProperty("total_pages")]
        public Int32 TotalPages { get; set; }

        [JsonProperty("data")]
        public IList<Data> DataItem { get; set; }
    }

    public class Data
    {
        [JsonProperty("id")]
        public Int32 Id { get; set; }

        [JsonProperty("userId")]
        public Int32 UserId { get; set; }

        [JsonProperty("userName")]
        public string UserName { get; set; }

        [JsonProperty("timestamp")]
        public Int64 TimeStamp { get; set; }

        [JsonProperty("txntype")]
        public string TxnType { get; set; }

        [JsonProperty("amount")]
        public string Amount { get; set; }

        [JsonProperty("location")]
        public LocationItem Location { get; set; }

        [JsonProperty("ip")]
        public string Ip { get; set; }
    }

    public class LocationItem
    {
        [JsonProperty("id")]
        public Int32 Id { get; set; }

        [JsonProperty("address")]
        public string Address { get; set; }

        [JsonProperty("city")]
        public string City { get; set; }

        [JsonProperty("zipCode")]
        public Int32 ZipCode { get; set; }
    }
}
