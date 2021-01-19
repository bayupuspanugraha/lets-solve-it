package main

import (
	"fmt"

	s "github.com/bayupuspanugraha/lets-solve-it-go/src/service"
)

func main() {
	fmt.Println("Normal way - Total Amount: ", s.GetTransactions(2, 8, 5, 50))
	fmt.Println("Concurrent way - Total Amount: ", s.GetTransactionsAsync(2, 8, 5, 50))
}
