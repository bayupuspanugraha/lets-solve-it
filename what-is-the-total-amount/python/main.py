import simple_request as sr
import async_request as ar

if __name__ == "__main__":
    print(f"(normal way) Total Amount: {sr.get_transactions(2, 8, 5, 50)}")
    print(f"(async way) Total Amount: {ar.get_transactions(2, 8, 5, 50)}")
