const axios = require('axios');

async function getDataFromUrl(userId, page) {
  try {
    var result = await axios.get(`https://jsonmock.hackerrank.com/api/transactions/search?userId=${userId}&page=${page}`);

    if(result.status === 200) {
      return result.data;
    }
  }catch(ex) {
    console.error(`Error found: ${ex}`);
  }

  return null;
}

function convertAmount(money) {
  var sanitiseAmount = money.replace("$", "").replace(",", "");
  var moneyInFloat = parseFloat(sanitiseAmount);

  return parseInt(Math.round(moneyInFloat));
}

function calculateAmount(datas, locationId, netStart, netEnd) {
  var total = 0;
  for(var data of datas) {
    if(data.location.id === locationId) {
      var ips = data.ip.split(".");
      var firstData = parseInt(ips[0]);
      if(firstData >= netStart && firstData <= netEnd) {
        total += convertAmount(data.amount);
      }
    }
  }

  return total;
}

async function handleRequest(userId, locationId, netStart, netEnd, page) {
  var resp = await getDataFromUrl(userId, page);
  var total = 0;
  if(resp.total > 0) {
    total += calculateAmount(resp.data, locationId, netStart, netEnd);
  }

  return [resp, total];
}

async function getTransactions(userId, locationId, netStart, netEnd) {
  var start = new Date().getTime();
  var [resp, total] = await handleRequest(userId, locationId, netStart, netEnd, 1);

  if(resp.total_pages > 1) {
   
    for(var page = 2; page <= resp.total_pages; page++) {      
        var [tempResp, tempTotal] = await handleRequest(userId, locationId, netStart, netEnd, page) ;
        total += tempTotal;
    } 
  }

  var end = new Date().getTime();
  var time = end - start;
  console.log(`Execution time: ${time} ms`);

  return total;
} 

async function getTransactionsParalel(userId, locationId, netStart, netEnd) {
    var start = new Date().getTime();
    var [resp, total] = await handleRequest(userId, locationId, netStart, netEnd, 1);
  
    if(resp.total_pages > 1) {
      var tasks = []
      for(var page = 2; page <= resp.total_pages; page++) {
        var task = new Promise(function(resolve) {
          handleRequest(userId, locationId, netStart, netEnd, page)
          .then(function(res) {
            resolve(res);
          });
        })  
  
        tasks.push(task);
      }
  
      var results = await Promise.all(tasks);
      for(var res of results) {
        var [tempRes, tempTot] = res; 
        total += tempTot;
      }
    }
  
    var end = new Date().getTime();
    var time = end - start;
    console.log(`Execution time: ${time} ms`);
  
    return total;
  } 

module.exports = {
    getTransactions,
    getTransactionsParalel
};