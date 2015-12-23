/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.casa.doughlatorRecipesGenerator;

import com.casa.doughlator.DoughRecipe;
import com.casa.doughlator.Ingredient;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;

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
   //private OutputMessagesListener listener;
    private File recipeXmlFileName;
    private String recipeObjListFileName;
    private String outputMsg;
    private ArrayList<DoughRecipe> doughRecipes = null;
    final private Logger logger;
    
    public DoughlatorRecipesGenerator(Object listener) 
    {
        recipeXmlFileName = null;
        recipeObjListFileName = null;
        
        logger = new Logger(listener);
        
        logger.setDebugEnable(true);
    }
  
    public int startConversion() 
    {
       DoughRecipe doughRecipe = null;
       Ingredient ing;
       int retVal = 1;
       
       if(recipeObjListFileName == null || 
               recipeObjListFileName.equals("") || 
               recipeXmlFileName == null)
       {
           retVal = -1;
           
           logger.log(
                   "Unspecified input/output file names",
                   Logger.ERROR_MSG);
       }
       else
       {
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
                  
                  logger.log("Starting conversion, wait please...", Logger.PROCCESS_MSG);
                    
                  while(eventReader.hasNext()){
                     XMLEvent event = eventReader.nextEvent();
                     switch(event.getEventType()){
                        case XMLStreamConstants.START_ELEMENT:
                           StartElement startElement = event.asStartElement();
                           String qName = startElement.getName().getLocalPart();

                           if (qName.equalsIgnoreCase("recipe")) {

                              outputMsg = "Start Element : recipe";
                              logger.log(outputMsg, Logger.PROCCESS_MSG);
                              
                              Iterator<Attribute> attributes = startElement.getAttributes();
                              String recipeName = attributes.next().getValue();
                              
                              outputMsg = "Recipe name : " + recipeName;
                              logger.log(outputMsg, Logger.PROCCESS_MSG);

                              /* New recipe found, create object */
                              doughRecipe = new DoughRecipe(recipeName);

                           } else if (qName.equalsIgnoreCase("ingredient")) {

                              outputMsg = "Start Element : ingredient";
                              logger.log(outputMsg, Logger.PROCCESS_MSG);
                              
                              Iterator<Attribute> attributes = startElement.getAttributes();

                              ing =  new Ingredient("","","",
                                      false,false,false);

                              while(attributes.hasNext())
                              { 
                                  Attribute atr;

                                  atr = attributes.next();

                                  if(atr.getName().getLocalPart().equalsIgnoreCase("name"))
                                  {
                                      String ingName = atr.getValue();
                                      outputMsg = "Ingredient name: " + ingName;
                                      logger.log(outputMsg, Logger.PROCCESS_MSG);

                                      ing.setName(ingName);
                                  }
                                  else if(atr.getName().getLocalPart().equalsIgnoreCase("quantity"))
                                  {
                                      String ingQty = atr.getValue();
                                      outputMsg = "Ingredient qty: " + ingQty;
                                      logger.log(outputMsg, Logger.PROCCESS_MSG);

                                      ing.setQty(ingQty);
                                  }
                                  else if(atr.getName().getLocalPart().equalsIgnoreCase("per"))
                                  {
                                      String ingPer = atr.getValue();
                                      outputMsg = "Ingredient per: " + ingPer; 
                                      logger.log(outputMsg, Logger.PROCCESS_MSG);

                                      ing.setPer(ingPer);
                                  }
                                  else if(atr.getName().getLocalPart().equalsIgnoreCase("baseIng"))
                                  {
                                      String baseIng = atr.getValue();
                                      outputMsg = "Ingredient refIng: " + baseIng; 
                                      logger.log(outputMsg, Logger.PROCCESS_MSG);
                                      
                                      boolean value = Boolean.valueOf(baseIng);

                                      ing.setBaseIngredient(value);
                                  }
                                  else if(atr.getName().getLocalPart().equalsIgnoreCase("refIng"))
                                  {
                                      String refIng = atr.getValue();
                                      outputMsg = "Ingredient refIng: " + refIng; 
                                      logger.log(outputMsg, Logger.PROCCESS_MSG);

                                      boolean value = Boolean.valueOf(refIng);
                                         
                                      ing.setReferenceIngredient(value);
                                  }
                                  else if(atr.getName().getLocalPart().equalsIgnoreCase("isLiquid"))
                                  {
                                      String isLiquid = atr.getValue();
                                      outputMsg = "Ingredient isLiquid: " + isLiquid; 
                                      logger.log(outputMsg, Logger.PROCCESS_MSG);

                                      boolean value = Boolean.valueOf(isLiquid);
                                       
                                      ing.setIsLiquid(value);
                                  }
                                  else if(atr.getName().getLocalPart().equalsIgnoreCase("refIng"))
                                  {
                                      String refIng = atr.getValue();
                                      outputMsg = "Ingredient refIng: " + refIng; 
                                      logger.log(outputMsg, Logger.PROCCESS_MSG);

                                      ing.setReferenceIngredient(true);
                                  }
                                  else
                                  {
                                      outputMsg = "Missed attribute " + 
                                              atr.getName().getLocalPart();
                                      logger.log(outputMsg, Logger.PROCCESS_MSG);
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
                              outputMsg = "Start Element : planner";
                              logger.log(outputMsg, Logger.PROCCESS_MSG);
                              
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
                                      outputMsg = "Missed attribute " + 
                                          atr.getName().getLocalPart();
                                      logger.log(outputMsg, Logger.PROCCESS_MSG);
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
                               outputMsg = "Missed element " + qName;
                               logger.log(outputMsg, Logger.PROCCESS_MSG);
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
                              outputMsg = "End Element : recipe";
                              logger.log(outputMsg, Logger.PROCCESS_MSG);
                             
                              /* Set recipe as builtin recipe */
                              doughRecipe.setBuiltInRecipe(true);
                              
                              /* Add recipe to container */
                              doughRecipes.add(doughRecipe);
                           }
                           else if(endElement.getName().getLocalPart().equalsIgnoreCase("ingredient"))
                           {
                              outputMsg = "End Element : ingredient";
                              logger.log(outputMsg, Logger.PROCCESS_MSG);
                           }
                           else if(endElement.getName().getLocalPart().equalsIgnoreCase("planner"))
                           {
                              outputMsg = "End Element : planner";
                              logger.log(outputMsg, Logger.PROCCESS_MSG);
                           }
                           else
                           {
                               outputMsg = "Missed element " + 
                                              endElement.getName().getLocalPart();
                               logger.log(outputMsg, Logger.PROCCESS_MSG);
                           }

                           break;
                     }		    
                  }
               } catch (FileNotFoundException e) {
                  retVal = -1;
                  e.printStackTrace();
                  logger.log(e, Logger.ERROR_MSG);
               } catch (XMLStreamException e) {
                  retVal = -1;
                  e.printStackTrace();
                  logger.log(e, Logger.ERROR_MSG);
            }
       }

       if(retVal!=-1)
       {
            if(doughRecipes!=null)
            {
                int cnt = 0;
                
                /* Print added recipes */
                for(DoughRecipe d:doughRecipes)
                {
                    cnt++;
                    logger.log("\n========================", Logger.INFO_MSG);
                    logger.log("\nRecipe ("+cnt+"): " + d, Logger.INFO_MSG);

                    for(Ingredient i:d.getIngredientsList())
                    {
                        logger.log(i, Logger.INFO_MSG);
                    }

                    logger.log("\nNotes file: " + 
                            d.getRecipePlanner().getNotesFileName(),
                            Logger.INFO_MSG);
                }

                logger.log("\nProccessed items: "+doughRecipes.size(),
                     Logger.INFO_MSG);
                   
                save(recipeObjListFileName);
                
                retVal = 1;
            }
       }
       
       return retVal;
    }
   
    public void save(String fileName)
    {
         FileOutputStream fo = null;
         ObjectOutputStream oos = null;

         try
         {
             logger.log("Saving into file " +
                     fileName, Logger.INFO_MSG);

             fo = new FileOutputStream(fileName);
             oos = new ObjectOutputStream(fo);

             for(DoughRecipe d:doughRecipes)
             {
                 oos.writeObject(d);
             }

             File createdFile = new File(fileName);
             logger.log("File <" + fileName + "> created successfully (" + 
                     createdFile.length() + " bytes)", Logger.INFO_MSG);
           
             fo.close();
             oos.close();
         } 

         catch(InvalidClassException ex)
         {
              logger.log(ex, Logger.ERROR_MSG);
         }
         catch (FileNotFoundException ex)
         {
              logger.log(ex, Logger.ERROR_MSG);
         } 
         catch (IOException ex) 
         {
              logger.log(ex, Logger.ERROR_MSG);
         } 
         finally 
         {
             try 
             {
                 fo.close();
             }catch (IOException ex) {
                 logger.log(ex, Logger.ERROR_MSG);
             }
         }    
    }

     public File getRecipeXmlFileName() {
        return recipeXmlFileName;
     }

     public void setRecipeXmlFileName(File recipeXmlFileName) {
        this.recipeXmlFileName = recipeXmlFileName;
     }

     public String getRecipeObjListFileName() {
        return recipeObjListFileName;
     }

     public void setRecipeObjListFileName(String recipeObjListFileName) {
        this.recipeObjListFileName = recipeObjListFileName;
     }
}