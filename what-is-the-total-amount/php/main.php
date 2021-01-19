<?php
require __DIR__ . '/vendor/autoload.php';

use GuzzleHttp\{Client, Promise};
use GuzzleHttp\Psr7\{Request, Response};
use GuzzleHttp\Promise\EachPromise;

// ** Needs to be defined global or at least 1 time and reuse it, to improve the speed performance
$client = new Client([
    'headers' => [
        'Content-Type' => 'application/json'
    ]
]);

function getDataFromURL($userId, $page)
{
    // ** Sync way
    $res = $GLOBALS['client']->request('GET', 'https://jsonmock.hackerrank.com/api/transactions/search?userId=' . $userId . '&page=' . $page);
    $status = $res->getStatusCode();

    if ($status == 200) {
        $result = json_decode($res->getBody());
        return $result;
    }

    return null;
}

function getDataFromURLAsync($userId, $page)
{
    // ** Async way
    $request = new Request('GET', 'https://jsonmock.hackerrank.com/api/transactions/search?userId=' . $userId . '&page=' . $page);

    return $GLOBALS['client']->sendAsync($request);
}

function convertAmount($money)
{
    $sanitiseMoney = str_replace('$', '', $money);
    $sanitiseMoney = str_replace(',', '', $sanitiseMoney);

    return intval(round(floatval($sanitiseMoney)));
}

function calculateAmount($datas, $locationId, $netStart, $netEnd)
{
    $total = 0;
    foreach ($datas as $data) {
        if ($data->location->id == $locationId) {
            $ips = explode('.', $data->ip);
            $firstData = $ips[0];

            if ($firstData >= $netStart && $firstData <= $netEnd) {
                $total = convertAmount($data->amount);
            }
        }
    }

    return $total;
}

function getTransactions($userId, $locationId, $netStart, $netEnd)
{
    $start = microtime(true);

    $total = 0;
    $response = getDataFromURL($userId, 1);
    if ($response->total > 0) {
        $total += calculateAmount($response->data, $locationId, $netStart, $netEnd);
    }

    if ($response->total_pages > 1) {
        for ($page = 2; $page <= $response->total_pages; $page++) {
            $response = getDataFromURL($userId, $page);
            if ($response->total > 0) {
                $total += calculateAmount($response->data, $locationId, $netStart, $netEnd);
            }
        }
    }

    $time_elapsed_secs = microtime(true) - $start;
    echo "Execution time: " . $time_elapsed_secs . " sec\n";
    return $total;
}

function getTransactionsAsync($userId, $locationId, $netStart, $netEnd)
{
    $start = microtime(true);

    $total = 0;
    $response = getDataFromURL($userId, 1);
    if ($response->total > 0) {
        $total += calculateAmount($response->data, $locationId, $netStart, $netEnd);
    }

    if ($response->total_pages > 1) {
        $totalPage = $response->total_pages;
        $promises = (function () use ($userId, $totalPage) {
            for ($page = 2; $page <= $totalPage - 1; $page++) {
                yield getDataFromURLAsync($userId, $page);
            }
        })();

        // ** EachPromise way
        $results = (new EachPromise($promises, [
            'concurrency' => $totalPage - 1, // how many concurrency that we gonna use
            'fulfilled' => function (Response $res) use ($locationId, $netStart, $netEnd, &$total) {
                $status = $res->getStatusCode();
                if ($status == 200) {
                    $result = json_decode($res->getBody());
                    if ($result->total > 0) {
                        $total += calculateAmount($result->data, $locationId, $netStart, $netEnd);
                    }
                }
            },
            'rejected' => function ($reason) {
                // handle promise rejected here
            }
        ]))->promise()->wait();

        //** Promise\settle way 
        //** Wait for the requests to complete, even if some of them fail 
        // $results = Promise\settle($promises)->wait();
        // foreach ($results as $response) {
        //     $res = $response['value'];
        //     $status = $res->getStatusCode();
        //     if ($status == 200) {
        //         $result = json_decode($res->getBody());
        //         if ($result->total > 0) {
        //             $total += calculateAmount($result->data, $locationId, $netStart, $netEnd);
        //         }
        //     }
        // }
    }

    $time_elapsed_secs = microtime(true) - $start;
    echo "Execution time: " . $time_elapsed_secs . " sec\n";
    return $total;
}

function main()
{
    $result = getTransactions(2, 8, 5, 50);
    echo 'Normal way - Total Amount: ' . $result;

    echo "\n";

    $result = getTransactionsAsync(2, 8, 5, 50);
    echo 'Concurrent way - Total Amount: ' . $result;
}

main();
