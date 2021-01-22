<?php
function calculateSimilarites($suffix, $original, &$counter) {
    for($it=0;$it<strlen($suffix);$it++){
        $suffixChar = substr($suffix, $it, 1);
        $originalChar = substr($original, $it, 1);

        if($suffixChar == $originalChar){
            $counter ++;
        }
        else {
            break;
        }
    }
}

function scanString($original, &$results) {
    $counter = 0;
    for($idx=-1;$idx<strlen($original) -1;$idx++){
        $suffix = $original;
        if($idx > -1){
            $suffix = substr($original, $idx+1);
        }
    
        calculateSimilarites($suffix, $original, $counter);
    }

    array_push($results, $counter);
}

function getCountSimilarities($datas) {
    $start = microtime(true);

    $results = [];

    foreach($datas as $original) {
        scanString($original, $results);
    }

    echo "Time Execution: " . (microtime(true) - $start) . " sec\n";
    echo "Results: ";
    return $results;
}

$samples = ["bcaabcbca", "ddcabaddcb", "rtortortop", "cakikicaci"];

print_r(getCountSimilarities($samples));