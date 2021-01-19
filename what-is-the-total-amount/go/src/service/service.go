package service

import (
	"encoding/json"
	"fmt"
	"math"
	"net/http"
	"strconv"
	"strings"
	"sync"
	"time"

	m "github.com/bayupuspanugraha/lets-solve-it-go/src/model"
)

func getDataFromURL(userID int32, page int32) m.Response {
	resp, err := http.Get(fmt.Sprintf("https://jsonmock.hackerrank.com/api/transactions/search?userId=%v&page=%v", userID, page))
	if err != nil {
		fmt.Println(err.Error())
		return m.Response{}
	}

	defer resp.Body.Close()
	var respObj m.Response
	err = json.NewDecoder(resp.Body).Decode(&respObj)
	if err != nil {
		fmt.Println(err.Error())
		return m.Response{}
	}

	return respObj
}

func convertAmount(money string) int32 {
	sanitiseMoney := strings.ReplaceAll(money, "$", "")
	sanitiseMoney = strings.ReplaceAll(sanitiseMoney, ",", "")

	moneyInFloat, _ := strconv.ParseFloat(sanitiseMoney, 64)
	return int32(math.Round(moneyInFloat))
}

func calculateAmount(datas []m.DataItem, locationID int32, netStart int32, netEnd int32) int32 {
	var total int32
	for _, data := range datas {
		if locationID == data.Location.Id {
			ips := strings.Split(data.Ip, ".")
			firstData, _ := strconv.Atoi(ips[0])
			if int32(firstData) >= netStart && int32(firstData) <= netEnd {
				total += convertAmount(data.Amount)
			}
		}
	}

	return total
}

func handleRequest(userID int32, locationID int32, netStart int32, netEnd int32, page int32) (m.Response, int32) {
	resp := getDataFromURL(userID, page)
	var total int32
	if resp.Total > 0 {
		total = calculateAmount(resp.Data, locationID, netStart, netEnd)
	}

	return resp, total
}

/*
GetTransactions is function to get total amount
*/
func GetTransactions(userID int32, locationID int32, netStart int32, netEnd int32) int32 {
	start := time.Now()

	// Step 1 need to know first collection data including with total pages
	// By then we can send it to new loop for the total_pages
	resp, total := handleRequest(userID, locationID, netStart, netEnd, 1)

	// Step 2 loop and handle similar process to get another pages total amount
	if resp.TotalPages > 1 {
		for page := int32(2); page <= resp.TotalPages; page++ {
			_, tempTotal := handleRequest(userID, locationID, netStart, netEnd, page)
			total += tempTotal
		}
	}

	elapsed := time.Since(start)
	fmt.Println("Time Executions: ", elapsed)

	return total
}

/*
GetTransactionsAsync is async function to get total amount by using wait group to improve the speed of each request process
*/
func GetTransactionsAsync(userID int32, locationID int32, netStart int32, netEnd int32) int32 {
	start := time.Now()

	// Step 1 need to know first collection data including with total pages
	// By then we can send it to new loop for the total_pages
	resp, total := handleRequest(userID, locationID, netStart, netEnd, 1)

	// Step 2 loop and handle similar process to get another pages total amount
	if resp.TotalPages > 1 {
		var wg sync.WaitGroup
		wg.Add(int(resp.TotalPages - 1)) // 'resp.TotalPages - 1' means we loop from iterator 2 to n
		for page := int32(2); page <= resp.TotalPages; page++ {
			go func(targetPage int32) {
				defer wg.Done()
				_, tempTotal := handleRequest(userID, locationID, netStart, netEnd, targetPage)
				total += tempTotal
			}(page)
		}
		wg.Wait()
	}

	elapsed := time.Since(start)
	fmt.Println("Time Executions: ", elapsed)

	return total
}
