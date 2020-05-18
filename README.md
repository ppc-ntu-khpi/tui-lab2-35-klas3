# Bank ClI

Даний репозиторій містить код **CLI** для роботи з клієнтами банку, в якому виконані **останнє** та всі [додаткові завдання](https://github.com/ppc-ntu-khpi/TUI-Lab2-Starter/blob/master/Lab%202%20-%20CLI/Lab%202%20-%20add.md).

# Приклад роботи з CLI

```
bank> help
help				- Show help
customer			- Show list of customers
customer 'index'		- Show customer details
account 'index' S/C		- Show current balance on customer's account
deposit 'customer' S/C 'sum'	- Deposit on customer's account
withdraw 'customer' S/C 'sum'	- Withdaw from customer's account
save				 - save all changes
exit				- Exit the app

bank> customers

This is all of your customers:

Last name	First Name	Balance
---------------------------------------
Doe		John		$2000.0
Mulder		Fox		$1000.0

bank> customer 1

This is detailed information about customer #1!

Last name	First Name	Account Type	Balance
-------------------------------------------------------
Mulder		Fox		Savings	$1000.0

bank> report
			CUSTOMERS REPORT
			================

Customer: Doe, John
    Checking Account: current balance is 2000.0

Customer: Mulder, Fox
    Savings Account: current balance is 1000.0

bank> account 1 S
$1000.0

bank> deposit 1 S 100
You've just deposit $100 on customer's account, current balance &1100.0

bank> withdraw 1 S 10
You've just withdraw $10 from customer's account, current balance &1090.0

bank> save

bank> exit
Exiting application
```

![](https://img.shields.io/badge/Made%20with-JAVA-red.svg)
![](https://img.shields.io/badge/Made%20with-%20Netbeans-brightgreen.svg)
![](https://img.shields.io/badge/Made%20at-PPC%20NTU%20%22KhPI%22-blue.svg) 
