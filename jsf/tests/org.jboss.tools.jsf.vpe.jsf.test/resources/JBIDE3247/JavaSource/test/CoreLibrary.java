package test;
import com.sun.facelets.tag.AbstractTagLibrary;


/**
 * 
 */

/**
 * @author mareshkau
 *
 */
public class CoreLibrary extends AbstractTagLibrary {
	  
	      public final static String Namespace = "http://jboss.org/jbosstools/test";
	  
	      public final static CoreLibrary Instance = new CoreLibrary();
	  
	      public CoreLibrary() {
	          super(Namespace);
	  
	          this.addTagHandler("if", IfHandler.class);
	  
	     }

  }
