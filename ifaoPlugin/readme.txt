=====================================================================================
     This readmeFile contains information, how to 'modify' the source files.
=====================================================================================

1.) Extract this Jar-File into a directory
e.g. extract everything to C:\temp\ifaoplugin

=====================================================================================

2.) Create a new PluginProject (within eclipse)

Select menu
-> File -> New -> Project...

Select within new-Wizzard
-> Plug-in Project

Select button
"Next >"

Enter projectname:
net.ifao.plugin

Deselect checkbox
[_] Use default location

and set as location the location, where jar file was extracted
e.g. C:\temp\ifaoplugin

Select button
"Next >"

Deselect Plug-in-options
[_] Generate an activator ...
[_] This plugin will make contributions to the UI

Select button
"Next >"

Deselect checkbox
[_] Create a plugin-in using one of the templates

Select button
"Finish"

=====================================================================================

3.) The project should contain errors, because the creation of the PluginProject 
modified/deleted the following files:

- META-INF\MANIFEST.MF 
- plugin.xml

You have to replace them with the 'original' files.

=====================================================================================

4.) Press refresh on the Project "net.ifao.plugin" and everything should be fine :-)

=====================================================================================
