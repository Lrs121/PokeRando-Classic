## Notes

- java.bind.xml Alternatives
    - [xmlbeans](http://xmlbeans.apache.org/)
    - [eclipselink](https://github.com/eclipse-ee4j/eclipselink)
- java BufferedImage is largely useless (i think, need better understanding of parent code)
- going file by file is slow but worth it
- read copyright at top of parent code, provides idea of usage
- SysConstants.java is not needed
- files in config/* and patches/* are now stored in res/raw folder 
    - use android calls instead
    - refactor parent code to adapt to change
- fill todo section    
    
## TODO

### Packages and Classes to "import"
[] constants
    [] GBConstants
    [] Gen1Constants
    [] Gen2Constants
    [] Gen3Constants
    [] Gen4Constants
    [] Gen5Constants
    [*] GlobalConstants
[] exceptions
    [] InvalidSupplementFilesException
    [] RandomizationException
    [] RandomizerIOException
[] gui - Read files but likely not needed
[] newnds
    [] CRC16
    [] NARCArchive
    [] NDSFile
    [] NDSRom
    [] NDSY9Entry
[] pokemon