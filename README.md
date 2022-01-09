# MStruct GUI

MStruct GUI is an extension of the [MStruct](https://github.com/xray-group/mstruct) program for MicroStructure analysis from powder diffraction data.

It is a desktop application providing a graphical interface for calling MStruct and processing its results. Besides configuring input parameters users can review the output data in a graphical form and use fitted parameters.

## Installation

##### The minimal distribution - `mstructgui-0.2-dist-min.zip`
1. Download the mstructgui-0.2-dist-min.zip. If not provided in other way it could be obtained from github (Code / Download ZIP / locate the file in the target folder).
2. Extract the content of the zip file to a “base directory”
3. Download and install a suitable JDK, i.e. JDK version 11 or newer. It is recommended to use  [BellSoft's Liberica JDK 11 LTS](https://bell-sw.com/pages/downloads/#/java-11-lts) as it was used for testing.
4. Open the `MStructGUI.bat` for editing and change the `java_home` variable to a location where the JDK is installed.
5. In the `MStructGUI.properties` edit following two paths to be able to use FOX for transforming cif files and MStruct for Optimization:
   - fox.exe.path
   - mstruct.exe.path
6. Optionally in the `MStructGUI.properties` the `mstructgui.open.on.startup` can be set to a file which will be opened automatically on startup.
7. Run the application with `MStructGUI.bat`.


##### The distribution with JDK - `mstructgui-0.2-dist-withJDK.zip`
If the mstructgui-0.2-dist-withJDK.zip is used instead of mstructgui-0.2-dist-min.zip the installation steps are the same but the step 3 and 4 can be omitted.

##### The externals - `mstructgui-0.2-only-externals.zip`
The mstructgui-0.2-only-externals.zip doesn’t contain the MStructGUI application. It contains only both external programs. It should be unpacked to the same directory where previously one of the above distributions was installed. It is packed with MStructGUI.properties which configures the paths so the step 5 can be skipped.


## Build and Assembly
Use [Maven](https://maven.apache.org/index.html) to build the application with the following command in the project root directory (i.e. where the pom.xml file is):

```
mvn clean install
```
The `mstruct-gui-0.2-dist-min.zip` should be created in the target directory. Other two distributables mentioned above will fail unless the paths defined in the `src/assembly/assembly-withJDK.xml` and `src/assembly/assembly-externals.xml` are correct.


## Documentation




