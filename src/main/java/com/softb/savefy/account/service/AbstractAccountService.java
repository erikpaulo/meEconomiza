package com.softb.savefy.account.service;

import com.softb.savefy.account.model.Account;

public abstract class AbstractAccountService {

    protected abstract void calcAccountBalance(Account checkingAccount);
}
