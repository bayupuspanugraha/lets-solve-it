using csharp.Services;
using System;

namespace csharp
{
    class Program
    {
        static void Main(string[] args)
        {
            var service = new Service();

            var result = service.GetTransactionsAsync(2, 8, 5, 50).Result;
            Console.WriteLine("Normal way - Total Amount: " + result);

            result = service.GetTransactionsParallelAsync(2, 8, 5, 50).Result;
            Console.WriteLine("Concurrent way - Total Amount: " + result);
        }
    }
}
