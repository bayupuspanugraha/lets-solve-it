import requests
from requests.exceptions import HTTPError
import json
from collections import namedtuple
from decimal import Decimal
import time


def get_data_from_url(session, userId, page):
    try:
        response = session.get(
            f"https://jsonmock.hackerrank.com/api/transactions/search?userId={userId}&page={page}")
        if response.status_code == 200:
            data = json.loads(response.text, object_hook=lambda d: namedtuple('X', d.keys())
                              (*d.values()))
            return data
        else:
            return None

    except HTTPError as http_err:
        print(f'HTTP error occurred: {http_err}')
    except Exception as err:
        print(f'Other error occurred: {err}')


def convert_amount(money):
    sanitiseMoney = money.replace("$", "").replace(",", "")
    moneyInDecimal = Decimal(sanitiseMoney)
    return moneyInDecimal


def calculate_amount(datas, locationId, netStart, netEnd):
    total = 0
    for data in datas:
        if locationId == data.location.id:
            ips = data.ip.split(".")
            firstData = int(ips[0])
            if firstData >= netStart and firstData <= netEnd:
                total += convert_amount(data.amount)
    return int(round(total, 0))


def get_transactions(userId, locationId, netStart, netEnd):
    # By using session, it could improve performance when doing multiple request.
    # Since this lib has tricky way to handle the multiple request.
    with requests.Session() as session:
        start_time = time.time()
        resp = get_data_from_url(session, userId, 1)
        total = 0
        if resp.total > 0:
            total += calculate_amount(resp.data, locationId, netStart, netEnd)

        if resp.total_pages > 1:
            for page in range(2, resp.total_pages + 1):
                resp = get_data_from_url(session, userId, page)
                if resp.total > 0:
                    total += calculate_amount(resp.data,
                                              locationId, netStart, netEnd)

        print("Time Executions: %s seconds" % (time.time() - start_time))
        return total
