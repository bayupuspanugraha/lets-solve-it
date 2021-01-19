package model

type Response struct {
	Page       interface{} `json:"page"`
	PerPage    int32       `json:"per_page"`
	Total      int32       `json:"total"`
	TotalPages int32       `json:"total_pages"`
	Data       []DataItem  `json:"data"`
}

type DataItem struct {
	Id        int32        `json:"id"`
	UserId    int32        `json:"userId"`
	UserName  string       `json:"userName"`
	TimeStamp int64        `json:"timestamp"`
	TxnType   string       `json:"txnType"`
	Amount    string       `json:"amount"`
	Location  LocationItem `json:"location"`
	Ip        string       `json:"ip"`
}

type LocationItem struct {
	Id      int32  `json:"id"`
	Address string `json:"address"`
	City    string `json:"city"`
	ZipCode int64  `json:"zipCode"`
}
