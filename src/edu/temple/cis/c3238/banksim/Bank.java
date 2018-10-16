package edu.temple.cis.c3238.banksim;

/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 * @author Modified by Charles Wang
 */
public class Bank {

    public static final int NTEST = 10;
    private final Account[] accounts;
    private long ntransacts = 0;
    private final int initialBalance;
    private final int numAccounts;

    public Bank(int numAccounts, int initialBalance) {
        this.initialBalance = initialBalance;
        this.numAccounts = numAccounts;
        accounts = new Account[numAccounts];
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new Account(this, i, initialBalance);
        }
        ntransacts = 0;
    }

    public void transfer(int from, int to, int amount) {
//        accounts[from].waitForAvailableFunds(amount);
        while (!(accounts[from].lock.isHeldByCurrentThread()) && !(accounts[to].lock.isHeldByCurrentThread())) {
            accounts[from].lock.lock();
            System.out.println(Thread.currentThread().toString() + " has my lock " + from);
            if (accounts[to].lock.isLocked()) {
                accounts[from].lock.unlock();
                System.out.println(Thread.currentThread().toString() + " released my lock " + from + " and going to sleep");
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {

                }
            } else {
                accounts[to].lock.lock();
                System.out.println(Thread.currentThread().toString() + " has to lock " + to);
            }
        }
        try {

            if (accounts[from].withdraw(amount)) {
                accounts[to].deposit(amount);
            }
            //if (shouldTest()) test();
        } finally {
            accounts[from].lock.unlock();
            System.out.println(Thread.currentThread().toString() + " released my lock " + from + " and going to sleep");
            accounts[to].lock.unlock();
            System.out.println(Thread.currentThread().toString() + " released my lock " + from + " and going to sleep");
            System.out.println(Thread.currentThread().toString() + "Transfer complete");
            try {
                Thread.sleep(1000);
            } catch (Exception e) {

            }
        }
    }

    public void test() {
        int sum = 0;
        for (Account account : accounts) {
            System.out.printf("%s %s%n",
                    Thread.currentThread().toString(), account.toString());
            sum += account.getBalance();
        }
        System.out.println(Thread.currentThread().toString()
                + " Sum: " + sum);
        if (sum != numAccounts * initialBalance) {
            System.out.println(Thread.currentThread().toString()
                    + " Money was gained or lost");
            System.exit(1);
        } else {
            System.out.println(Thread.currentThread().toString()
                    + " The bank is in balance");
        }
    }

    public int size() {
        return accounts.length;
    }

    public boolean shouldTest() {
        return ++ntransacts % NTEST == 0;
    }

}
