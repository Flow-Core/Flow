# Flow

Welcome to _Flow_! A programming language designed specifically for networking development. Need to test your server or create a quick demo? You've come to the right place.

## Getting started

- [ ] Download [the MSI installer](https://github.com/Flow-Core/Flow/blob/main/flow-1.0.0.msi) and follow the instructions
- [ ] Add ```C:\Program Files\flow``` to the ```PATH``` environment variable


## Project structure

### ```libs/```
Place your desired library ```.jar``` files in here.

**Required**: ```flow-stdlib.jar``` is required for basic types and behaviors. You can find it [here](https://github.com/Flow-Core/Flow/blob/main/flow-stdlib/flow-stdlib.jar).

### ```src/```
Here you can place all of your source files with your _Flow_ code. Source files must have the ```.fl``` or the ```.flow``` extensions.

You can break files into packages by separating them into folders and adding the corresponding line at the top of the file:
```
package your.package
```

**Required**: One of the following functions must appear in one of the root source files:
```
func main() {}
func main(Array<String> args) {}
```

## Usage

In order to build and run your _Flow_ project, run the following command in the command line:
```
flow run -c -p path/to/project/folder
```

## Next Steps

- Check out the Flow Documentation: [Coming Soon]
