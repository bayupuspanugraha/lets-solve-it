function calculateSimilarities(suffix, original) {
    var counter = 0;
    for(var it=0;it<suffix.length;it++) {
        if(suffix.substr(it,1) == original.substr(it,1)) {
            counter++;
        } else {
            break;
        }
    }

    return counter;
}

function scanString(original) {
    var counter = 0;
    for(var idx=-1;idx<original.length -1;idx++) {
        var suffix = original;
        if(idx>-1) {
            suffix = original.substr(idx+1);
        }

        counter += calculateSimilarities(suffix, original);
    }
    
    return counter;
}

function getCountSimilarities(datas) {
    var start = new Date().getTime();

    var results = [];
    for(var data of datas) {
        results.push(scanString(data));
    }

    var end = new Date().getTime();

    console.log(`Execution Time: ${end - start} ms`)

    return results;
}

samples = ["bcaabcbca", "ddcabaddcb", "rtortortop", "cakikicaci"]
console.log(`Result: [${getCountSimilarities(samples)}]`);