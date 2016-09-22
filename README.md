# docu-properties-maven-plugin
Este plugin lleva a cabo dos tareas fundamentales:
  - Genera la documentación de las propiedades de cualquier tipo de fichero de configuración.
  - Compila ficheros de configuración para diferentes entornos a partir de un único fichero. De esta forma, ya no es necesario mantener mútliples ficheros para los diferentes entornos en los que se encuentre nuestra aplicación, bastará con mantener nuestro fichero de propiedaddes "docu-properties".

Para llevar a cabo estas tareas, es necesario añadir en los comentarios de los ficheros de propiedades, ciertas marcas a las que llamaremos "anotaciones" que capacitan el procesamiento del texto y generan uno o n ficheros resultado. 

Existen dos bloques distintos de mojos en este proyecto:
- Documentación de propiedades:
    - **docu-properties:** Transforma la información obtenido de los ficheros de propiedades alojados en el directorio de recursos de un proyecto en varios ficheros **HTML**, **EXCEL** o **Texto plano** de documentación.
    - **docu-properties-input:** Transforma la información obtenido de n ficheros de propiedades alojados en n directorios en varios ficheros **HTML**, **EXCEL** o **Texto plano** de documentación.
- Compilación de ficheros por entorno:
    - **write-properties:** Lee n ficheros de propiedades alojados en el directorio de recursos de un proyecto y con la información obtenida compila n ficheros de propiedades resultado.
    - **docu-properties-input-write:** Lee n ficheros de propiedades alojados en n directorios, y con la información obtenida compila n ficheros de propiedades resultado

## Sistema de anotaciones
Este plugin se basa en un sistema de etiquetas situado en los bloques de comentarios de los ficheros de propiedades para su funcionamiento. Entendiendo un bloque de comentario como el conjunto de líneas de comentarios que están separados de otros bloques por un espacio o por un carácter imprimible que no sea un caracter de comentario. Se entiende por etiqueta cualquier palabra dentro de un bloque de comentarios precedida por el caracter `@`. Existen dos bloques de anotaciones distintos:
- **SimpleComment:** Esta anotación permite imprimir un bloque de comentarios tal cual está escrito en el fichero. 

Ejemplo:
```docu-properties
# @SimpleComment
# !Hello World
```
Resultado:
```docu-properties
# !Hello World
```
- **Documented:** Esta anotación permite identificar el inicio de los diferentes elementso que acompañan a una propiedad. Su efecto termina con la aparición de cualquier texto imprimible por debajo del último bloque de comentario en el que esté incluido el elemento.
 
Ejemplo:
```docu-properties
# @Documented
my.property=value
```
Resultado:
```docu-properties
my.property=value
```
### Anotaciones de la propiedad
Todas estas anotaciones son opcionales y configurables. A continuación se muestran sus valores por defecto y su funcionalidad:
- **Description:** Permite añadir una descripción a la propiedad. 
- **State:** Permite añadir la obligatoriedad de la propiedad. 
- **Values:** Permite añadir posibles valores de la propiedad.
- **Example:** Permite añadir un ejemplo de valor a la propiedad. 
- **VisibleWithValue:** Permite que las propiedades con esta anotación sólamente sean visibles si tienen un valor asociado. En caso cotrario, las propiedades con esta anotación, no serán visualizadas.
- **Cualquier Otro valor:** Cualquier otra palabra precedida del caracter de anotación, se entiende como el valor asociado a un determinado entorno de compilación

Ejemplo para una ejecución de ``ENTORNO_A``:
```docu-properties
# @Documented
# @Description Descripción de la propiedad
# @State (OBLIGATORIO)
# @VisibleWithValue
# @ENTORNO_A valueA
# @ENTORNO_B valueB
my.property.a=

# @Documented
# @Description Descripción de la propiedad
# @State Estado (OBLIGATORIO)
# @VisibleWithValue
# @ENTORNO_B valueB
my.property.b=
```
Resultado:
```docu-properties
# Descripción de la propiedad
# Estado (OBLIGATORIO)
my.property.a=valueA
```

## Configuración del plugin
Como cualaquier plugin maven, requiere de una configuración a nivel del POM del proyecto. A continuación se muestra un ejemplo de configuración para el mojo: ``docu-properties-input-write``
```xml
<plugin>
	<groupId>es.home.plugins</groupId>
	<artifactId>docu-properties-maven-plugin</artifactId> 
	<version>${version}</version>
	<executions>
		<execution>
			<id>generate-properties</id> 
			<phase>generate-sources</phase>
			<goals>
				<goal>docu-properties-input-write</goal> 
			</goals>
		</execution>
	</executions>
	<configuration>
		<inputs>
			<input>${user.dir}/properties</input>
		</inputs>
		<environments>
			<environment>ENVIRONMENT_A</environment>
			<environment>ENVIRONMENT_B</environment>
		</environments>
		<write.environment>${properties.write.environment}</write.environment>
		<read.charsets>
			<read.charset>UTF-8</read.charset>
			<read.charset>ISO-8859-1</read.charset>
		</read.charsets>
		<output>${install.path}/conf</output>
	</configuration>
</plugin>
```
Dentro del bloque ``<configuration>`` se establece la configuración del plugin. A continuación se muestran todos los elementos configurables:
### docu-properties:docu-properties

Nombre | Tipo | Descripción | Valor por defecto
------ | ---- | ----------- | -----------------
annotationString | String[]	| Cadena que identifica que una línea es un comentario | [#]
asignationAnnotationString | String	| Permite idnetificar el signo de asignación de la propiedad | =
attrdescription	| String | Atributo que indica la descripción de una propiedad | Description
attrexample	| String | Atributo que indica un ejemplo de una propiedad | Example
attrinit | String | Atributo que indica el inicio de una propiedad a documentar | Documented
attrstate | String | Atributo que indica el estado de una propiedad | State
attrvalues | String | Atributo que indica los valores posibles de una propiedad | Values
attrvisiblewithvalue || Atributo que indica que un elemento es visible sólo si tiene valor | VisibleWithValue.
environments | String[] | Entornos de desarrollo a documentar. Cada uno de los entornos especificados se comportarán como un elemento más de las propiedades 
extensions | String[] | Extensiones de los ficheros que pueden ser procesados | ["properties"]
output | String	| Atributo que indica el directorio donde se guardará el recurso final | ${project.build.directory}/docu-properties.
read.charsets | String[] | Atributo que determina el conjunto de charset por defecto para la lectura de los ficheros de propiedades. | ["UTF-8","ISO-8859-1","UTF-16"]  
type | DocumenterType |	Atributo que indica el nombre del fichero donde se guardará la documentación de las propiedades. Los posibles valores son: EXCEL,PLAIN,HTML | EXCEL
write.charset | String | Atributo que determina el charset por defecto para la escritura de los ficheros de propiedades. | UTF-8.

### docu-properties:docu-properties-input

Nombre | Tipo | Descripción | Valor por defecto
------ | ---- | ----------- | -----------------
annotationString | String[]	| Cadena que identifica que una línea es un comentario | [#]
asignationAnnotationString | String	| Permite idnetificar el signo de asignación de la propiedad | =
attrdescription	| String | Atributo que indica la descripción de una propiedad | Description
attrexample	| String | Atributo que indica un ejemplo de una propiedad | Example
attrinit | String | Atributo que indica el inicio de una propiedad a documentar | Documented
attrstate | String | Atributo que indica el estado de una propiedad | State
attrvalues | String | Atributo que indica los valores posibles de una propiedad | Values
attrvisiblewithvalue || Atributo que indica que un elemento es visible sólo si tiene valor | VisibleWithValue.
environments | String[] | Entornos de desarrollo a documentar. Cada uno de los entornos especificados se comportarán como un elemento más de las propiedades 
extensions | String[] | Extensiones de los ficheros que pueden ser procesados | ["properties"]
inputs | String[] | Conjunto de directorioa donre ir a buscar los recursos | 
output | String	| Atributo que indica el directorio donde se guardará el recurso final | ${project.build.directory}/docu-properties.
read.charsets | String[] | Atributo que determina el conjunto de charset por defecto para la lectura de los ficheros de propiedades. | ["UTF-8","ISO-8859-1","UTF-16"]  
type | DocumenterType |	Atributo que indica el nombre del fichero donde se guardará la documentación de las propiedades. Los posibles valores son: EXCEL,PLAIN,HTML | EXCEL
write.charset | String | Atributo que determina el charset por defecto para la escritura de los ficheros de propiedades. | UTF-8.

### docu-properties:docu-properties-input-write

Nombre | Tipo | Descripción | Valor por defecto
------ | ---- | ----------- | -----------------
addDescription | boolean | Determina si debe o no ser incluida la descripción en el fichero final | true
addExample | boolean | Determina si deben o no ser incluidos los ejemplos en el fichero final | false
addState | boolean	| Determina si debe o no ser incluido el estado de la propiedad en el fichero final | false.
annotationString | String[]	| Cadena que identifica que una línea es un comentario | [#]
asignationAnnotationString | String	| Permite idnetificar el signo de asignación de la propiedad | =
attrdescription	| String | Atributo que indica la descripción de una propiedad | Description
attrexample	| String | Atributo que indica un ejemplo de una propiedad | Example
attrinit | String | Atributo que indica el inicio de una propiedad a documentar | Documented
attrstate | String | Atributo que indica el estado de una propiedad | State
attrvalues | String | Atributo que indica los valores posibles de una propiedad | Values
attrvisiblewithvalue || Atributo que indica que un elemento es visible sólo si tiene valor | VisibleWithValue.
environments | String[] | Entornos de desarrollo a documentar. Cada uno de los entornos especificados se comportarán como un elemento más de las propiedades 
extensions | String[] | Extensiones de los ficheros que pueden ser procesados | ["properties"]
inputs | String[] | Conjunto de directorioa donre ir a buscar los recursos | 
output | String	| Atributo que indica el directorio donde se guardará el recurso final | ${project.build.directory}/docu-properties.
read.charsets | String[] | Atributo que determina el conjunto de charset por defecto para la lectura de los ficheros de propiedades. | ["UTF-8","ISO-8859-1","UTF-16"]  
write.charset | String | Atributo que determina el charset por defecto para la escritura de los ficheros de propiedades. | UTF-8.
write.environment | String | Entorno para el que será compilado el fichero resultado de propiedades

### docu-properties:write-properties

Nombre | Tipo | Descripción | Valor por defecto
------ | ---- | ----------- | -----------------
addDescription | boolean | Determina si debe o no ser incluida la descripción en el fichero final | true
addExample | boolean | Determina si deben o no ser incluidos los ejemplos en el fichero final | false
addState | boolean	| Determina si debe o no ser incluido el estado de la propiedad en el fichero final | false.
annotationString | String[]	| Cadena que identifica que una línea es un comentario | [#]
asignationAnnotationString | String	| Permite idnetificar el signo de asignación de la propiedad | =
attrdescription	| String | Atributo que indica la descripción de una propiedad | Description
attrexample	| String | Atributo que indica un ejemplo de una propiedad | Example
attrinit | String | Atributo que indica el inicio de una propiedad a documentar | Documented
attrstate | String | Atributo que indica el estado de una propiedad | State
attrvalues | String | Atributo que indica los valores posibles de una propiedad | Values
attrvisiblewithvalue || Atributo que indica que un elemento es visible sólo si tiene valor | VisibleWithValue.
environments | String[] | Entornos de desarrollo a documentar. Cada uno de los entornos especificados se comportarán como un elemento más de las propiedades 
extensions | String[] | Extensiones de los ficheros que pueden ser procesados | ["properties"]
output | String	| Atributo que indica el directorio donde se guardará el recurso final | ${project.build.directory}/docu-properties.
read.charsets | String[] | Atributo que determina el conjunto de charset por defecto para la lectura de los ficheros de propiedades. | ["UTF-8","ISO-8859-1","UTF-16"]  
write.charset | String | Atributo que determina el charset por defecto para la escritura de los ficheros de propiedades. | UTF-8.
write.environment | String | Entorno para el que será compilado el fichero resultado de propiedades
