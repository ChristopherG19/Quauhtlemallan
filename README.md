# Quauhtlemallan

El objetivo general de este proyecto es desarrollar una aplicación móvil educativa que fomente el interés por aprender más sobre la geografía y temas culturales de Guatemala, dejando la misma como una base para futuras mejoras o adaptaciones en estos u otros campos de estudio. 

El proyecto se centra en temas de **gamificación** y alinea todos los componentes a la rica diversidad cultural del país. Se utilizaron herramientas y técnicas actuales como **Jetpack Compose**, arquitectura **MVVM** y refinamiento de un modelo de Inteligencia Artificial con datasets recopilados manualmente. Estas características buscan ofrecer una experiencia educativa única e interactiva.

## Tabla de Contenidos

- [Estructura del Proyecto](#estructura-del-proyecto)
- [Características](#características)
- [Evaluación del Proyecto](#evaluación-del-proyecto)
  
## Estructura del Proyecto

El repositorio cuenta con los siguientes directorios principales:

- **`.github/workflows`**: Contiene un workflow encargado de actualizar la branch `develop` luego de cada cambio en `master` para mantener un flujo adecuado utilizando GitFlow.
- **`Android_App`**: Proyecto de Android Studio que representa la aplicación móvil.
- **`Google_Cloud`**: Archivos utilizados para desplegar un servicio en **Google Cloud Run** que conecta la aplicación con modelos alojados en Hugging Face.
- **`Kukul_Bot`**: Contiene datasets y notebooks necesarios para el fine-tuning de un modelo **Llama 2**. Este modelo se especializa en la generación de texto relacionado con la cultura y geografía de Guatemala.

## Características

- **Interfaz amigable e intuitiva**: Desarrollada con Jetpack Compose y Kotlin.
- **Modelo de IA especializado**: Refinado con datos culturales y geográficos de Guatemala.
- **Sistema gamificado**: Incluye retos y niveles relacionados con la diversidad cultural del país.

## Evaluación del Proyecto

Se evaluaron grupos de diferentes edades para determinar:
- La facilidad de uso de la aplicación.
- Su aporte en la reducción de brechas de conocimiento.
- La efectividad de la gamificación como herramienta educativa.
- La efectividad del fine-tunning realizado para especializar un modelo de generación de texto.

Los resultados indicaron que las aplicaciones gamificadas aportan significativamente al aprendizaje y pueden ser utilizadas como método de enseñanza en temas culturales y geográficos.
