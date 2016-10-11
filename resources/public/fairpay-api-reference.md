# fairpay

A Clojure powered web service providing fair wage calculations for low-income workers.

## Project Site

More info and the code can be found [here](https://github.com/ryanquincypaul/fairpay).

## Table-of-Contents

* [calculate-weekly-pay](#weekly)

## Preface

All of these calls must be appended to the end of the website hosting the service. Currently that is...

`http://fairpay.azurewebsites.net/fairpay`

## <a name="weekly"></a>Calculate-Weekly-Pay

Calculates fair payment for a week of work given a minimum-wage, hours worked, and gross pay.

`/calculate-weekly-pay?minimum-wage=7.25&hours-worked=50&gross-wages=200`

### Parameters

`minimum-wage`: Could be Federal, State, or Local. One option to populate this field is to call the [minimum-wage](https://github.com/ryanquincypaul/minimum-wage) web service.

`hours-worked`: Hours worked in a weekly pay period.

`gross-wages`: Gross pay over a weekly pay period.

### Response

```JSON
{
  "is-error": false,
  "error": {},
  "fairpay": false,
  "wage-breakdown": {
    "normal-hours": "40",
    "normal-pay": "290.00",
    "overtime-hours": "10",
    "overtime-pay": "108.75",
    "total-pay": "398.75",
    "reported-gross-wages": "200.00",
    "difference": "198.75"
  }
}
```
