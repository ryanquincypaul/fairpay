# fairpay

A Clojure powered web service providing fair wage calculations as well as determination of FLSA compliance.

## Example
This is currently hosted at the url below.
```
curl http://fairpay.azurewebsites.net/fairpay/weekly-earnings?minimum_wage=7.25&hours_worked=50&gross_wages=300
```

Output

```JSON
{
	"is_error": "false",
	"error": {},
	"fairpay": "false",
	"wage_breakdown": {
		"normal_hours": "40",
		"normal_pay": "290.00",
		"overtime_hours": "10",
		"overtime_pay": "108.75",
		"total_pay": "398.75",
		"reported_gross_wages": "300.00",
		"difference": "98.75"
	}
}
```

## Usage

This service can be consumed by anything that is set up to handle JSON responses. Federal and State minimum wage data can be found using the [minimum-wage](https://github.com/ryanquincypaul/minimum-wage) web service.

See it in use within an Angular 2 web application at [fairpayWeb](https://github.com/ryanquincypaul/fairpayWeb).

## Motivation

I wrote this as a microservice to a future web application that will be available for low-income earners to check if they are getting paid appropriately according to the FLSA. This could have just been done on the web application itself, but I wanted to consolidate the business logic in case further apps are written to serve the same function. 

## API Reference

A description of the calls can be found on the [wiki](https://github.com/ryanquincypaul/fairpay/wiki).

## Install

Pull down the project locally and run `lein deps` and then `lein ring uberwar` to generate a WAR file that you can host on your Java server of choice.

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Test

Run `lein test` in the main project folder.

## TODO

I hope to add/fix the following
* Add documentation_url to the error responses

## Contributions

If you are interesting in contributing to this or any other of my projects, contact me [here](mailto:ryan.quincy.paul@gmail.com)

## License

Copyright (C) 2016 Ryan Paul

Distributed under the Eclipse Public License, the same as Clojure.
