package cdi;

import java.math.BigDecimal;

public interface Account { 
	
	public BigDecimal getBalance(); 
	
	public User getOwner(); 
	
	public void withdraw(BigDecimal amount); 
	
	public void deposit(BigDecimal amount); 
	
}
