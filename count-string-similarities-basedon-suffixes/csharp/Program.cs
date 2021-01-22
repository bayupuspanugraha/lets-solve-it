using System;
using System.Diagnostics;
using System.Collections.Generic;
using System.Linq;

namespace csharp
{
    class Program
    {
        private void CalculateSimilarities(String suffix, String original, ref int counter)
        {
            for(var it=0;it<suffix.Length;it++)
            {
                if(suffix.Substring(it, 1) == original.Substring(it, 1)) 
                {
                    counter++;
                } else {
                    break;
                }
            }
        }
        
        private void ScanString(String original, ref IList<int> results)
        {
            int counter = 0;
            for(var idx=-1;idx<original.Length-1;idx++)
            {
                var suffix = original;
                if(idx > -1) 
                {
                    suffix = original.Substring(idx+1);
                }

                CalculateSimilarities(suffix, original, ref counter);
            }

            results.Add(counter);
        }

        public IList<int> GetCountSimilarities(String[] datas)
        {
            Stopwatch st = new Stopwatch();
            st.Start();

            IList<int> results = new List<int>();

            foreach(var data in datas)
            {
                ScanString(data, ref results);
            }

            st.Stop();

            Console.WriteLine($"Execution Time: {st.ElapsedMilliseconds} ms");
            
            return results;
        }

        static void Main(string[] args)
        {
            var samples = new String[] {"bcaabcbca", "ddcabaddcb", "rtortortop", "cakikicaci"};

            Program prog = new Program();
            Console.WriteLine($"Result: [{string.Join(',', prog.GetCountSimilarities(samples))}]");
 
        }
    }
}
