import android

def fact(n):
        result = 1
        for i in range(1,n):
            result *= i
        return result

def run():
    return "Returned Fact(120) = " + str(fact(12))
