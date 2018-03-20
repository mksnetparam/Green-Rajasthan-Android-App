/*
 * Copyright (C) 2011 Prasanta Paul, http://prasanta-paul.blogspot.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package greenbharat.cdac.com.greenbharat.helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Adds the ability to JSON Lib (in Android) to parse JSON data into Bean Class. It
 * is based on Android's (org.json.*)
 * 
 * @author Nitesh Sharma
 *
 */
public class JSONHandler {

	String TAG = "cdac";
	
	/**
	 * Parse a JSONObject and create instance of BeanClass. The JSONObject can internally contain
	 * any kind of Objects and Arrays. The field names of BeanClass should exactly match as the JSON
	 * property name.
	 * 
	 * TODO:
	 * 1. Support JSONArray as initial argument. Note: JSONArray can be
	 * intermediate argument during the recursion process.
	 * 2. Support primitive/custom Array. At present it supports only ArrayList
	 * 3. Support custom data types which doesn't belong to -same Base Package-
	 *  
	 * @param jsonStr JSON String
	 * @param beanClass Java Bean class 
	 * @param basePackage Base package name which includes all Bean classes 
	 * @return Instance of of the Bean Class with parsed value embedded. 
	 * @throws Exception
	 */
	public Object parse(String jsonStr, Class beanClass, String basePackage) throws Exception
	{
		/*
		 * This will be a recursive method
		 * 1. Read all member variables of BeanClass
		 * 2. Read values from JSON
		 * 3. If it is an ArrayList, call parse() with its type class
		 * 4. If it is a Custom Class, call parse() with its type class
		 */
		Object obj = null;
		JSONObject jsonObj = new JSONObject(jsonStr);
		
		if(beanClass == null){
			p("Class instance is Null");
			return null;
		}
		
		p("Class Name: "+ beanClass.getName());
		p("Package: "+ beanClass.getPackage().getName());
		
		// Read Class member fields
		Field[] props = beanClass.getDeclaredFields(); 
		
		if(props == null || props.length == 0)
		{
			/*
			 * This class has no fields
			 */
			p("Class "+ beanClass.getName() +" is empty");
			return null;
		}
		
		// Create instance of this Bean class
		obj = beanClass.newInstance();
		
		// Set value of each member variable of this object
		for(int i=0; i<props.length; i++)
		{
			String fieldName = props[i].getName();
			
			// Filter public and static fields
			if(props[i].getModifiers() == (Modifier.PUBLIC | Modifier.STATIC))
			{
				// Skip static fields
				p("Modifier: "+ props[i].getModifiers());
				p("Static Field: "+ fieldName +" ....Skip");
				continue;
			}
			
			// Date Type of Field 
			Class type = props[i].getType();
			String typeName = type.getName();
			
			/*
			 * If the type is not primitive- [int, long, java.lang.String, float, double] and ArrayList/List
			 * Check for Custom type  
			 */
			if(typeName.equals("int")) // int type
			{
				p("Primitive Field: "+ fieldName + " Type: "+ typeName);
				// Handle Integer data
				// Get associated Set method- Need to mention Method Argument
				Class[] parms = {type};
				Method m = beanClass.getDeclaredMethod(getBeanMethodName(fieldName, 1), parms);
				m.setAccessible(true);
				
				// Set value- JSONObject.getInt()
				try{
					m.invoke(obj, jsonObj.getInt(fieldName));
				}catch(Exception ex){
					p("Error: "+ ex.toString());
				}
			}
			else if(typeName.equals("long"))
			{
				p("Primitive Field: "+ fieldName + " Type: "+ typeName);
				// Handle Integer data
				// Get associated Set method- Need to mention Method Argument
				if(!fieldName.equals("serialVersionUID")) {
					Class[] parms = {type};
					Method m = beanClass.getDeclaredMethod(getBeanMethodName(fieldName, 1), parms);
					m.setAccessible(true);

					// Set value- JSONObject.getLong()
					try {
						m.invoke(obj, jsonObj.getLong(fieldName));
					} catch (Exception ex) {
						p("Error: " + ex.toString());
					}
				}
			}
			else if(typeName.equals("java.lang.String"))
			{
				p("Primitive Field: "+ fieldName + " Type: "+ typeName);
				// Handle Integer data
				// Get associated Set method- Need to mention Method Argument
				Class[] parms = {type};
				Method m = beanClass.getDeclaredMethod(getBeanMethodName(fieldName, 1), parms);
				m.setAccessible(true);
				
				// Set value- JSONObject.getString()
				try{
					m.invoke(obj, jsonObj.getString(fieldName));
				}catch(Exception ex){
					p("Error: "+ ex.toString());
				}
			}
			else if(typeName.equals("double"))
			{
				p("Primitive Field: "+ fieldName + " Type: "+ typeName);
				// Handle Integer data
				// Get associated Set method- Need to mention Method Argument
				Class[] parms = {type};
				Method m = beanClass.getDeclaredMethod(getBeanMethodName(fieldName, 1), parms);
				m.setAccessible(true);
				
				// Set value- JSONObject.getDouble()
				try{
					m.invoke(obj,  jsonObj.getDouble(fieldName));
				}catch(Exception ex){
					p("Error: "+ ex.toString());
				}
			}
			else if(type.getName().equals(List.class.getName()) || 
					type.getName().equals(ArrayList.class.getName())){
				// ArrayList
				// Find out the Generic i.e. Class type of its content
				p("ArrayList Field: "+ fieldName + " Type: "+ type.getName() +" field: "+ props[i].toGenericString());
				String generic = props[i].getGenericType().toString();
				p("ArrayList Generic: "+ generic);
				
				if(generic.indexOf("<") != -1){
					// extract generic from <>
					String genericType = generic.substring(generic.lastIndexOf("<")+1, generic.lastIndexOf(">"));
					if(genericType != null){
						// Further refactor it
						//showClassDesc(Class.forName(genericType), basePackage);
						// It is a JSON Array- loop through the Array and create instances
						p("Generic Type: "+ genericType);
						JSONArray array = null;
						try{
							array = jsonObj.getJSONArray(fieldName);
						}catch(Exception ex){
							p("Error: "+ ex.toString());
							array = null;
						}
						/*
						 * If it is of Primitive types, loop through the JSON Array and read tem into an 
						 * ArrayList
						 */
						if(array == null)
							continue;
						p("JSON Array Size: "+ array.length());
						ArrayList arrayList = new ArrayList();
						for(int j=0; j<array.length(); j++)
						{
							arrayList.add(parse(array.getJSONObject(j).toString(), Class.forName(genericType), basePackage));
						}
						
						// Set the value
						Class[] parms = {type};
						Method m = beanClass.getDeclaredMethod(getBeanMethodName(fieldName, 1), parms);
						m.setAccessible(true);
						m.invoke(obj,  arrayList);
					}
					
				}
				else{
					// No generic defined
					// ArrayList<Generic_Type>
					generic = null;
				}
			}
			else if(typeName.startsWith(basePackage)){
				// Custom Class
				// Handle Custom class
				// Get associated Set method- Need to mention Method Argument
				p("Custom class Field: "+ fieldName + " Type: "+ typeName);
				Class[] parms = {type};
				Method m = beanClass.getDeclaredMethod(getBeanMethodName(fieldName, 1), parms);
				m.setAccessible(true);
				
				// Set value- do a recursive Call to read values of this custom class
				try{
					JSONObject customObj = jsonObj.getJSONObject(fieldName);
					if(customObj != null)
						m.invoke(obj, parse(customObj.toString(), type, basePackage));
				}catch(JSONException ex){
					p("Error: "+ ex.toString());
					// TODO: set default value
				}
			}
			else{
				// Skip it
				p("Skip, Field: "+ fieldName +" Type: "+ typeName +" SimpleTypeName: "+  type.getSimpleName());
			}
		}
		return obj;
	}
	
	/**
	 * Generate Get/Set method of BeanClass fields
	 * @param fieldName
	 * @param type
	 * @return
	 */
	private String getBeanMethodName(String fieldName, int type){
		if(fieldName == null || fieldName == "")
			return "";
		String method_name = "";
		if(type == 0)
			method_name = "get";
		else
			method_name = "set";
		method_name += fieldName.substring(0, 1).toUpperCase();
		
		if(fieldName.length() == 1)// Field name is of 1 char
			return method_name;
		
		method_name += fieldName.substring(1);
		return method_name;
	}
	
	private void p(String msg){
		//System.out.println(msg);
		//Log.v(TAG, "# "+ msg);
	}
}
