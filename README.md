ArchTrace
=========

Traditional techniques of traceability detection and management are
not equipped to handle evolution. This is a problem for the field
of software architecture, where it is critical to keep synchronized
an evolving conceptual architecture with its realization in an
evolving code base. ArchTrace is a new tool that addresses this
problem through a policy-based infrastructure for automatically
updating traceability links every time an architecture or its code
base evolves. ArchTrace is pluggable, allowing developers to choose
a set of traceability management policies that best match their
situational needs and working styles.

Please, read the following papers to find more information about
ArchTrace:

* [MURTA, L. G. P.; VAN DER HOEK, A.; WERNER, C. M. L.; "Continuous and Automated Evolution of Architecture-to-Implementation Traceability Links". Automated Software Engineering: An International Journal, volume 15, issue 1, pages 75-107, 2008.](http://dx.doi.org/10.1007/s10515-007-0020-6)
* [MURTA, L. G. P.; VAN DER HOEK, A.; WERNER, C. M. L.; "ArchTrace: Policy-Based Support for Managing Evolving Architecture-to-Implementation Traceability Links". International Conference on Automated Software Engineering (ASE), Tokyo, pages 135-144, 2006.](http://dx.doi.org/10.1109/ASE.2006.16)
* [MURTA, L. G. P.; VAN DER HOEK, A.; WERNER, C. M. L.; "ArchTrace: A Tool for Keeping in Sync Architecture and its Implementation". Tools Session of the Brazilian Symposium on Software Engineering (SBES), Florianopolis, pages 127-132, 2006.](http://www2.ic.uff.br/~leomurta/papers/murta2006.pdf)

Copyright (c) 2004 Regents of the University of California.  
Copyright (c) 2004 COPPE/UFRJ.  
Copyright (c) 2013 IC/UFF.  
All rights reserved.

Authors
-------

* Leonardo Gresta Paulino Murta (leomurta@ic.uff.br)

* Andre van der Hoek (andre@ics.uci.edu)

* Claudia Maria Lima Werner (werner@cos.ufrj.br)

Download
--------

ArchTrace is available under two different editions:

* [Standalone edition](https://github.com/gems-uff/archtrace/raw/master/release/ArchTrace.jnlp)
* [ArchStudio edition](https://github.com/gems-uff/archtrace/raw/master/release/ArchTrace.zip)

Obs1.: Both editions need Java 5.0.

Obs2.: The standalone edition is deployed using Java Web Start.

Obs3.: The Subversion connection can be done via command line interface (PATH variable configured with "svn.exe") or Java Native Interface.

Obs4.: Some [hooks](https://github.com/gems-uff/archtrace/raw/master/release/hooks.zip) are needed in the subversion repository to allow the execution of "Automatic update" policy, related to configuration items evolution.

Evaluation results
------------------

We evaluated ArchTrace over the [Odyssey](http://reuse.cos.ufrj.br/site/pt/index.php?option=com_content&task=view&id=20&Itemid=22) project. The raw results are available [here](https://htmlpreview.github.com/?https://github.com/gems-uff/archtrace/blob/master/release/odyssey.html).

Included software
-----------------

ArchStudio, Menage, xArchLibs  
Copyright (c) 2000-2004 Regents of the University of California.  
All rights reserved.

Subversion  
Copyright (c) 2000-2004 CollabNet.  
All rights reserved.

License terms
-------------

Redistribution and use in source and binary forms are permitted
provided that the above copyright notice and this paragraph are
duplicated in all such forms and that any documentation,
advertising materials, and other materials related to such
distribution and use acknowledge that the software was developed
by the University of California, Irvine, Federal University of
Rio de Janeiro, and Fluminense Federal University. The name of 
the Universities may not be used to endorse or promote products 
derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
