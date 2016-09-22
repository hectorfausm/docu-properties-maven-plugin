package es.home.plugin;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.*;
public class TestPlugin {

    @Before
    public void setUp() {
    }


    @Test
    public void testOneItemCollection() {
    	Path wiki_path = Paths.get("D:\\hfaus\\Docu\\Maven\\chapter5\\docuPropertiesMaven\\src\\test\\java\\es\\home\\plugin", "TestPlugin.java");

        Charset charset = Charset.forName("ISO-8859-1");
        try {
          List<String> lines = Files.readAllLines(wiki_path, charset);

          for (String line : lines) {
        	  if(line.isEmpty()){
        		  System.out.println("-");
        	  }else{
        		  System.out.println(line);
        	  }
          }
        } catch (IOException e) {
          System.out.println(e);
        }
    }
}