#  Copyright (c) 2021, Idriss Riouak <idriss.riouak@cs.lth.se>
#  All rights reserved.

#  Redistribution and use in source and binary forms, with or without
#  modification, are permitted provided that the following conditions are met:

#  1. Redistributions of source code must retain the above copyright notice,
#  this list of conditions and the following disclaimer.

#  2. Redistributions in binary form must reproduce the above copyright notice,
#  this list of conditions and the following disclaimer in the documentation
#  and/or other materials provided with the distribution.

#  3. Neither the name of the copyright holder nor the names of its
#  contributors may be used to endorse or promote products derived from this
#  software without specific prior written permission.

#  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
#  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
#  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
#  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
#  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
#  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
#  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
#  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
#  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
#  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
#  POSSIBILITY OF SUCH DAMAGE.


import sys
from PyPDF2 import PdfFileMerger
import os
filename = sys.argv[1]

#cmd = 'cp ' + filename + ' ' + filename + '.java'
# os.system(cmd)
cmd = 'vim ' + filename+'.java' + \
    ' -c ":hardcopy > ' + filename + '.ps ''" -c ": q"'
os.system(cmd)
cmd = 'ps2pdf ' + filename + '.ps ' + filename+'_tmp.pdf'
os.system(cmd)

merger = PdfFileMerger()
merger.append(filename+'_tmp.pdf')
merger.append(filename+'_FNext_CFG.pdf')
merger.write(filename+'_CFG.pdf')

merger.close()

cmd = 'mv ' + filename + '_FNext_CFG.pdf ' + filename + '.pdf '
os.system(cmd)



cmd = 'rm ' + filename + '_tmp.pdf ' + filename + '.ps ' + \
    filename + '_FNext_CFG.pdf '

os.system(cmd)

cmd = 'mv ' + filename + '_FNext_CFG.dot ' + filename + '.dot '
os.system(cmd)
