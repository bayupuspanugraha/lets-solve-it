import asyncio
import json
import time

from functools import wraps
from collections import namedtuple
from decimal import Decimal
from aiohttp import ClientSession
from asyncio.proactor_events import _ProactorBasePipeTransport


def silence_event_loop_closed(func):
    @wraps(func)
    def wrapper(self, *args, **kwargs):
        try:
            return func(self, *args, **kwargs)
        except RuntimeError as e:
            if str(e) != 'Event loop is closed':
                raise
    return wrapper


_ProactorBasePipeTransport.__del__ = silence_event_loop_closed(
    _ProactorBasePipeTransport.__del__)


async def get_data_from_url(session, userId, page):
    try:
        response = await session.get(f"https://jsonmock.hackerrank.com/api/transactions/search?userId={userId}&page={page}")

        if response.status == 200:
            data = json.loads(await response.read(), object_hook=lambda d: namedtuple('X', d.keys())
                              (*d.values()))
            return data
        else:
            return None

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


async def handle_minitask(session, userId, locationId, netStart, netEnd, page):
    total = 0
    resp = await get_data_from_url(session, userId, page)

    if resp.total > 0:
        total += calculate_amount(resp.data, locationId, netStart, netEnd)

    return (resp, total)


async def handle_async_process(userId, locationId, netStart, netEnd):
    async with ClientSession() as session:
        start_time = time.time()
        resp, total = await handle_minitask(session, userId, locationId, netStart, netEnd, 1)

        if resp.total_pages > 1:
            tasks = []
            for page in range(2, resp.total_pages + 1):
                task = handle_minitask(
                    session, userId, locationId, netStart, netEnd, page)
                tasks.append(task)

            responses = await asyncio.gather(*tasks)
            for _, respTotal in responses:
                total += respTotal
        print("Time Executions: %s seconds" % (time.time() - start_time))

        return total


def get_transactions(userId, locationId, netStart, netEnd):
    # version 3.3
    # loop = asyncio.get_event_loop()
    # future = asyncio.ensure_future(handle_async_process(
    #     userId, locationId, netStart, netEnd))
    # result = loop.run_until_complete(future)
    # loop.close()

    # version 3.7
    result = asyncio.run(handle_async_process(
        userId, locationId, netStart, netEnd))
    return result
