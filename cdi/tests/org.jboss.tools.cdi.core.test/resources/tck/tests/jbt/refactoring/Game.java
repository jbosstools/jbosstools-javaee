package org.jboss.jsr299.tck.tests.jbt.refactoring;

import java.io.Serializable;

//import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.NamingException;

@SessionScoped
@Named
public class Game implements Serializable
{
	private static final long serialVersionUID = 12L;

	@Named("myInner")
	public static class MyInner {
		
	}
   private int number;
   private int guess;
   private int smallest;
   private int biggest;
   private int remainingGuesses;
   
   @Inject @MaxNumber private int maxNumber;

   @Inject private int[] maxNumber2;

   @Inject Generator[] generator;

   @Inject @Random Instance<Integer> randomNumber;
   
   public Game() throws NamingException {}

   public int getNumber()
   {
      return number + 9;
   }
   
   public int getGuess()
   {
      return guess;
   }
   
   public void setGuess(int guess)
   {
	   String s = "#{another.kkk}";
      this.guess = guess;
   }
   
   public int getSmallest()
   {
      return smallest;
   }
   
   public int getBiggest()
   {
      return biggest;
   }
   
   public int getRemainingGuesses()
   {
      return remainingGuesses;
   }

   public boolean check() 
   {
      if (guess>number)
      {
         biggest = guess - 1;
      }
      if (guess<number)
      {
         smallest = guess + 1;
      }
      remainingGuesses--;
      return (guess == number);
   }

//   @PostConstruct
   public void reset()
   {
      this.smallest = 0;
      this.guess = 0;
      this.remainingGuesses = 10;
      this.biggest = maxNumber;
      this.number = randomNumber.get();
   }
}

@Named("myLocal")
class MyLocal {
	
}
