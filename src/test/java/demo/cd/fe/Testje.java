package demo.cd.fe;

import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class Testje {


    @Test
    public void testIt() {

        List<Object> testList = new ArrayList<>();

        testList.add(new Integer(1));
        testList.add("ToBeRemoved");
        testList.add(new Integer(2));
        
        List<Object> newList = removeBasicAuthInterceptorIfPresent(testList);

        assertEquals(1, newList.get(0));        
        assertEquals(2, newList.get(1));        
    }
    
	private List<Object> removeBasicAuthInterceptorIfPresent(List<Object> interceptors) { 
	
 		List<Object> updatedInterceptors = new ArrayList<>(interceptors); 
 		
 		Iterator<Object> iterator = updatedInterceptors.iterator(); 
 		
 		while (iterator.hasNext()) { 

 			if (iterator.next() instanceof String) { 
 			   System.err.println("@@@@@ removing String");
 				iterator.remove(); 
 			} 

 		} 
 		return updatedInterceptors; 
 	} 
}