
import time

def calculateSimilarites(suffix, original):
    counter = 0
    for it in range(0, len(suffix)):
        suffixChar = suffix[it:it+1]
        originalChar = original[it:it+1]

        if suffixChar == originalChar:
            counter = counter + 1
        else:
            break
    return counter

def scanString(original, results):
    originalLength = len(original)
    counter = 0
    for idx in range(-1, originalLength -1):
        suffix = original
        if idx > -1:
            suffix = original[idx+1:originalLength]
    
        counter = counter + calculateSimilarites(suffix, original)

    results.append(counter)

def getCountSimilarities(datas):
    start_time = time.time()
    results = []

    for original in datas:
        scanString(original, results)

    print("Time Execution: %s seconds" % (time.time() - start_time))
    return results

if __name__ == "__main__":
    samples = ["bcaabcbca", "ddcabaddcb", "rtortortop", "cakikicaci"]

    print(f"Result: {getCountSimilarities(samples)}")