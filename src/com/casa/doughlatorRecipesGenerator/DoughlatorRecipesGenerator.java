/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.casa.doughlatorRecipesGenerator;

import com.casa.doughlator.DoughRecipe;
import com.casa.doughlator.Ingredient;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class DoughlatorRecipesGenerator 
{
   static String recipeXmlFileName;
   static String recipeObjList;
   static ArrayList<DoughRecipe> doughRecipes = null;
   
   public static void main(String[] args) 
   {
      DoughRecipe doughRecipe = null;
      Ingredient ing;
      recipeXmlFileName = "recipes.xml"; 
      recipeObjList = "recipeList.list";
      
      try 
      {
         XMLInputFactory factory = XMLInputFactory.newInstance();
         XMLEventReader eventReader =
         factory.createXMLEventReader(
            new FileReader(recipeXmlFileName));

            if(eventReader!=null)
            {
                /* Create arraylist */
                doughRecipes = new ArrayList<>();
            }
            while(eventReader.hasNext()){
               XMLEvent event = eventReader.nextEvent();
               switch(event.getEventType()){
                  case XMLStreamConstants.START_ELEMENT:
                     StartElement startElement = event.asStartElement();
                     String qName = startElement.getName().getLocalPart();
                     
                     if (qName.equalsIgnoreCase("recipe")) {
                        
                        System.out.println("Start Element : recipe");
                        Iterator<Attribute> attributes = startElement.getAttributes();
                        String recipeName = attributes.next().getValue();
                        System.out.println("Recipe name : " + recipeName);
                        
                        /* New recipe found, create object */
                        doughRecipe = new DoughRecipe(recipeName);
                        
                     } else if (qName.equalsIgnoreCase("ingredient")) {
                        
                        System.out.println("Start Element : ingredient");
                        Iterator<Attribute> attributes = startElement.getAttributes();
                        
                        ing = new Ingredient("","","",false,false);
                        
                        while(attributes.hasNext())
                        { 
                            Attribute atr;
                            
                            atr = attributes.next();
                            
                            if(atr.getName().getLocalPart().equalsIgnoreCase("name"))
                            {
                                String ingName = atr.getValue();
                                System.out.println("Ingredient name: " + ingName);
                                
                                ing.setName(ingName);
                            }
                            else if(atr.getName().getLocalPart().equalsIgnoreCase("quantity"))
                            {
                                String ingQty = atr.getValue();
                                System.out.println("Ingredient qty: " + ingQty);
                                
                                ing.setQty(ingQty);
                            }
                            else if(atr.getName().getLocalPart().equalsIgnoreCase("per"))
                            {
                                String ingPer = atr.getValue();
                                System.out.println("Ingredient per: " + ingPer);
                                
                                ing.setPer(ingPer);
                            }
                            else
                            {
                                System.out.println("Missed attribute " + 
                                        atr.getName().getLocalPart());
                            }
                            
                        }
                      
                        if(doughRecipe!=null)
                        {
                            /* Add ingredient to recipe */
                            doughRecipe.getIngredientsList().add(ing);
                        }
                     }
                     else if(qName.equalsIgnoreCase("planner"))
                     {
                        System.out.println("Start Element : planner");
                        Iterator<Attribute> attributes = startElement.getAttributes();
                        String notesFileName = null;
                        
                        while(attributes.hasNext())
                        {
                            Attribute atr = attributes.next();
                            
                            if(atr.getName().getLocalPart().equalsIgnoreCase("notesFileName"))
                            {
                                notesFileName = atr.getValue();
                            }
                            else
                            {
                                System.out.println("Missed attribute " + 
                                    atr.getName().getLocalPart());
                            }
                        }
                        
                        if(doughRecipe!=null)
                        {
                            if(notesFileName!=null)
                            {
                                doughRecipe.getRecipePlanner().setNotesFileName(notesFileName);
                            }
                        }
                     }
                     else
                     {
                         System.out.println("Missed element " + 
                                        qName);
                     }
                     
                     break;
                  case XMLStreamConstants.CHARACTERS:
                     Characters characters = event.asCharacters();
                    
                     /*if(characters.getData().equals("")==false)
                     {
                        System.out.println("Found characters: " + 
                                        characters);
                     }*/
                     
                     break;
                     
                  case  XMLStreamConstants.END_ELEMENT:
                     
                     EndElement endElement = event.asEndElement();
                     
                     if(endElement.getName().getLocalPart().equalsIgnoreCase("recipe")){
                        System.out.println("End Element : recipe");
                        System.out.println();
                        
                        /* Add recipe to container */
                        doughRecipes.add(doughRecipe);
                     }
                     else if(endElement.getName().getLocalPart().equalsIgnoreCase("ingredient"))
                     {
                        System.out.println("End Element : ingredient");
                        System.out.println();
                     }
                     else if(endElement.getName().getLocalPart().equalsIgnoreCase("planner"))
                     {
                        System.out.println("End Element : planner");
                        System.out.println(); 
                     }
                     else
                     {
                         System.out.println("Missed element " + 
                                        endElement.getName().getLocalPart());
                     }
                     
                     break;
               }		    
            }
         } catch (FileNotFoundException e) {
            e.printStackTrace();
         } catch (XMLStreamException e) {
            e.printStackTrace();
      }
   
      if(doughRecipes!=null)
      {
        /* Print added recipes */
        for(DoughRecipe d:doughRecipes)
        {
            System.out.println("\nReceta:" + d);
            
            for(Ingredient i:d.getIngredientsList())
            {
                System.out.println(i);
            }
            
            System.out.println("\nArchivo de notas: " + 
                    d.getRecipePlanner().getNotesFileName());
        }
        
        save(recipeObjList);
      }  
   }
   
   static public void save(String fileName)
   {
        FileOutputStream fo = null;
        ObjectOutputStream oos = null;
   
        try
        {
            System.out.println("Guardando en archivo " +
                    fileName);
             
            fo = new FileOutputStream(fileName);
            oos = new ObjectOutputStream(fo);
            
            for(DoughRecipe d:doughRecipes)
            {
                oos.writeObject(d);
            }
            
            System.out.println("Archivo guardado");
            
            fo.close();
            oos.close();
        } 
       
        catch(InvalidClassException ex)
        {
             Logger.getLogger(
                     DoughlatorRecipesGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(
                    DoughlatorRecipesGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(
                    DoughlatorRecipesGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } 
        finally 
        {
            try 
            {
                fo.close();
            }catch (IOException ex) {
                Logger.getLogger(
                        DoughlatorRecipesGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }    
   }
}