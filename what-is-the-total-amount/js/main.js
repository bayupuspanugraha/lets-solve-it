const service = require('./src/service');
 
var mainApp = async function() {
    var result = await service.getTransactions(2, 8, 5, 50);
    console.log(`Normal way - Total Amount: ${result}`);
    
    result = await service.getTransactionsParalel(2, 8, 5, 50);
    console.log(`Concurrent way - Total Amount: ${result}`);
}

mainApp();

