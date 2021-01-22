package main

import (
	"fmt"
	"time"
)

func calculateSimilarities(suffix string, original string, counter *int) {
	for it := 0; it < len(suffix); it++ {
		suffixChar := suffix[it : it+1]
		oriChar := original[it : it+1]

		if suffixChar == oriChar {
			*counter++
		} else {
			break
		}
	}
}

func scanString(original string, results *[]int) {
	dataLength := len(original)
	var counter int
	for idx := -1; idx < dataLength-1; idx++ {
		suffix := original
		if idx > -1 {
			prefixIndex := idx + 1
			suffix = original[prefixIndex:dataLength]
		}

		calculateSimilarities(suffix, original, &counter)
	}

	*results = append(*results, counter)
}

func getCountSimilarities(datas []string) []int {
	start := time.Now()

	var results []int
	for _, data := range datas {
		scanString(data, &results)
	}

	elapsed := time.Since(start)

	fmt.Println("Time Execution: ", elapsed)

	return results
}

func main() {
	samples := []string{"bcaabcbca", "ddcabaddcb", "rtortortop", "cakikicaci"}

	fmt.Println("Result: ", getCountSimilarities(samples))
}
