#ABDIEL Development Guide#

##Overview##

This document explains how to generate and run the ABDIEL editor from the Git repositories. Familiarity with Eclipse EMF, GMF, the Epsilon framework and Eclipse plug-in development is assumed.

The process entails the following four major steps:

- Set up the ABDIEL project in Epsilon
- Run the circuit model transformation
- Generate the visual circuit editor
- Run the visual circuit editor

##Set up ABDIEL in Epsilon##

In this step, you will get Epsilon, check out the ABDIEL core source, and configure it as an Eclipse project, generating the Java code for the ABDIEL model.

**Get Epsilon**

First, if you haven't done so yet, download Epsilon [here](https://eclipse.org/epsilon/download/).  This guide has been developed against Epsilon 1.1.0.201309101707/Win32; it is unknown whether the steps work on different versions/platforms.

**Get ABDIEL Core source**

Next, create a working copy of the [ABDIEL core repository](https://github.com/rjimenezh/abdiel) within the Epsilon workspace.  Since the ABDIEL core was not committed as an Eclipse project, the next steps are importing the project and generating its model code.

**Import the project**

Choose **File|New|Project...**, then **Empty EMF Project**, then click **Next**.  Write `abdiel` as the project name and click **Finish**.

**Generate ABDIEL code**

Carry out the following steps:

- Right-click the `abdiel.emf` file within `model` and choose **Generate ECore Model**
- Right-click the resulting `abdiel.ecore` file and choose **Eugenia|Generate EMF editor**

There should now be several Java files within the `src` source folder, as well as several new projects in the workspace.

**Register the ABDIEL EPackages**

Right-click the generated `abdiel.ecore` file and choose **Register EPackages**.  This will allow Epsilon to recognize the ABDIEL namespace URI, which will be required for the next major step.

##Run the circuit model transformation##

As outlined in [the ABDIEL paper](http://www.modelesis.com/wp-content/uploads/2015/04/Using-ADLs-for-software-assisted-electronics-design.pdf), ABDIEL works by allowing users to create a *part library*, where part types are specified in terms of their pins and ports.  The resulting part library is run through an Epsilon Transformation Language (ETL) script in order to generate a new ECore model where each relevant element in the library gets a suitable concrete ECore equivalent, in order to obtain proper GMF support for the part library.

In this step, you will configure and run the circuit model transformation, in order to be able to generate the visual circuit editor.

**(Optional) Tweak your part library**

The ABDIEL code includes a sample part library named `library.abdiel` within the `model` folder.  You can use this file to generate the base circuit editor.  If you wish to tweak the part library, run the ABDIEL Eclipse project as an Eclipse application, and use the generated ECore editor from the previous step to generate or edit your own part file.  The detailed steps to do this are out of the scope of this guide; familiarity with EMF should enable you to start the editor, and reading the ABDIEL paper will give you an idea of how to edit the model.

**Configure the ETL transformation**

Carry out the following steps:

- Create a new ETL transformation by clicking **Run|Run Configurations…**, then clicking **ETL Transformation** and then clicking the **New** button
- Type **GenCircuit** as the transformation name (the uppermost box in the dialog).  Select the `circuit.etl` file as the transformation source
- In the **Models** tab, click **Add…** and then choose **EMF Model**.  Name the model **Source**, and choose `library.abdiel` as the model file.  The ABDIEL metamodel URI should be added automatically to the metamodels list.  Click **OK** to finish
- Add another EMF model, naming it **Target**, choosing **circuit.ecore** as the model file, and ensuring the **http://www.eclipse.org/emf/2002/Ecore**  metamodel is included in the metamodels list.  For this model, you _must_ clear the **Read on load:** checkbox
- Add a final model, this time an Registered EMF EPackage.  Name it **ECore** and choose **http://www.eclipse.org/emf/2002/Ecore** as the meta-model URI

**Run the ETL transformation**

After adding the models, click **Apply**, then **Run**.  A new window should appear by the bottom of the Epsilon workspace, titled **Console|Epsilon**, and the text `Transformation finished` should display.  A new file named `circuit.ecore` should be created under `abdiel/model`.

You are now ready to generate the visual circuit editor.

##Generate the visual circuit editor##

In this step, you will configure final prerequisites, then use Epsilon’s EuGenia to generate a GMF editor from your circuit model.

**Configure ABDIEL plug-in exports**

Carry out the following steps:

- Right-click the `circuit.ecore` file and choose **Eugenia|Generate EMF editor**
- Make `ext` an Eclipse source folder by right-clicking the `abdiel` project’s topmost folder and choosing **New|Source Folder**, typing **ext** as the folder’s name, and clicking **Finish**
- Make classes in `ext`visible to dependent plugins.  To do this, open the `plugin.xml`file.  Click the **Build** lower tab, then select the library entry named **.** (“dot”) on the left panel of the **Runtime Information** section.  Click the **Add Folder…** button and select **ext** and then click **OK**.  Save and close `plugin.xml`

**Generate the GMF editor**

Now, right-click the `circuit.ecore` file and choose **Eugenia|Generate GMF editor**.  After a while, you will get a message stating the code generation was successful, as well as several new projects in the workspace.

**Set up the ABDIEL analysis plug-in**

ABDIEL not only allows you to model electronic circuits, but to carry out automated design analysis. A basic set of analysis plug-ins can be added by following these steps:

- Create a working copy of the [ABDIEL analysis repository](https://github.com/rjimenezh/abdiel.analysis) within the Epsilon workspace
- Choose **File|New|Project...**, then **Plug-in Project**, then click **Next**.  Write `abdiel` as the project name, and click **Next** twice.  Clear the **Create a plug-in using one of the templates** checkbox, and click **Finish**

**Copy part icons**

For the included sample library parts, custom icons for each concrete part are also included.  Copy the contents of `abdiel/icons/full/obj16` to `abdiel.edit/icons/full/obj16` (note the added **.edit** on the second path), overwriting any existing files.  Note that if you tweak the included `library.abdiel` file, you may have to edit and/or rename some of the icon files.  If you re-generate the ABDIEL EMF editor, _you will lose the icons_ and they will revert to GMF defaults, so make sure you copy any custom icons to a separate location before re-generating the ABDIEL EMF editor.

##Run the visual circuit editor##

At this point, you can run the editor by running the **abdiel.diagram** plug-in as an Eclipse application; however, to ensure the editor can be easily exported (and hence run outside of Epsilon), you should first create an appropriate product configuration, as outlined next.

**Create a custom product extension**

Carry out the following steps:

- Open the **abdiel.diagram** `plugin.xml` file and navigate to the **Extensions** lower tab
- Click **Add…** and select the **org.eclipse.core.runtime.products** extension point, then click **Finish**
- Back in the **Extension** tab, type **product** as the ID and **ABDIEL** as the name in the **Extension Details** panel to the right
- On the left panel, right-click your new extension point and choose **New|product**.  Type **org.eclipse.ui.ide.workbench** in the **application** field, and **ABDIEL** in the **name** field
- Right-click the newly created **product** element and choose **New|property**.  Type **appName** in the **name** field, and **ABDIEL** in the **value** field
- Save and close `plugin.xml`

**Create a custom product configuration**

Based on the product extension defined in the previous step, you will now create a product configuration.  Carry out the following steps:

- Right click the **abdiel.diagram** project’s topmost folder and choose **New|Other…**, then choose **Product Configuration** and click **Next**
- Type **abdiel.product** in the **File name** field, and select (if not automatically done by Eclipse) **abdiel.diagram.product** within **Use an existing product:**, then click **Finish**
- Navigate to the **Dependencies** lower tab in the `abdiel.product` file editor
- Click **Remove All**, then add the following plug-ins: **abdiel.diagram**, **abdiel.analysis**, **org.eclipse.ui.ide.application**, **org.eclipse.ui.navigator.resources**, **org.eclipse.gmf.runtime.diagram.ui.resources.editor.ide** (read [this article](http://www.modelesis.com/?p=204) for an explanation of these dependencies)
- Click **Add Required Plug-ins**.  For reference, as of this writing this yields a total of 151 plug-ins
- Save and close `abdiel.product`

**(Optional) Tweak the perspective and new file wizard classes**

The generated ABDIEL Circuit GMF editor uses the GMF stock perspective and new file wizard.  The stock `DiagramEditorPerspective` class doesn’t include the Project Explorer and Problem Views, while the `WizardNewFileCreationPage` class will default to the user’ s OS home folder as opposed to the select project’s root folder.  Ideally, you should change these classes to enhance the usability of your circuit editor.

While these changes were made to the original version of ABDIEL, as they take place over generated code they have not been committed to the Git repository.  A later version may provide for a clean inclusion of these changes to the repository.

**Run the product**

At long last, you can now run the visual circuit editor by right-clicking the `abdiel.product` file and choosing **Run|Eclipse Application**.  You will find the **Circuit Diagram** file type under the **Examples** category in the new file wizard.

By using Eclipse’s product export capabilities (outside the scope of this guide), you can create a stand-alone copy of the visual circuit editor, which can run on any computer with the appropriate Java runtime environment.  A copy of the original ABDIEL editor thus exported can be downloaded [here](https://drive.google.com/open?id=0B7xNv8V2dHDaTHBrWkxOaFRYSjA&authuser=0).

