#include <iostream>
#include <list>
#include <chrono> 

using namespace std::chrono; 
using namespace std;

void calculateSimilarities(string suffix, string original, int *counter)
{
  for(int it=0;it<suffix.length();it++)
  {
    if(suffix.substr(it,1) == original.substr(it,1))
    {
      *counter = *counter + 1;
    } else {
      break;
    }
  }
}

void scanString(string original, list<int> *results)
{
  int counter = 0;
    
  for(int idx=0;idx<original.length();idx++)
  {
    string suffix = original;
    int targetIdx = idx - 1;
    if(targetIdx>-1)
    {
      suffix = original.substr(targetIdx+1, original.length());
    } 

    calculateSimilarities(suffix, original, &counter);
  }

  results->push_back(counter);
}

list<int> getCountSimilarities(string datas[],int length) {
  auto start = high_resolution_clock::now(); 

  list<int> results;
  int arrCounter = 0;

  for(int d=0;d<length;d++) {
    scanString(datas[d], &results);
  }

  auto stop = high_resolution_clock::now(); 
  auto duration = duration_cast<milliseconds>(stop - start); 
  cout << "Time Execution: " << duration.count() << " ms" << endl;

  return results;
}

int main() {
  string samples[4] = {"bcaabcbca", "ddcabaddcb", "rtortortop", "cakikicaci"};
  int length = sizeof(samples)/sizeof(samples[0]);
  list<int> results = getCountSimilarities(samples, length);
  list <int> :: iterator it;
  cout << "Result:" << endl << "[";
  int counter = 0;
  for(it = results.begin(); it != results.end(); it++)
  {
    cout<< *it;
    if(counter < results.size() -1)
    {
      cout << ",";
    }
    counter++;
  }

  cout << "]";
}