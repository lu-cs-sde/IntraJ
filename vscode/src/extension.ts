'use strict';
import * as net from 'net';
import * as path from 'path';

import {  workspace, window, ExtensionContext } from 'vscode';
import { LanguageClient, LanguageClientOptions, ServerOptions, StreamInfo } from 'vscode-languageclient';

export function activate(context: ExtensionContext) {
    let script = 'java';
    let args = ['-jar',context.asAbsolutePath(path.join('intraj.jar')), '-vscode'];
    let serverOptions: ServerOptions = {
        run : { command: script, args: args },
        debug: { command: script, args: args} 
    };
    

    let clientOptions: LanguageClientOptions = {
        documentSelector: [{ scheme: 'file', language: 'java' }],
        synchronize: {
            configurationSection: 'java',
            fileEvents: [ workspace.createFileSystemWatcher('**/*.java') ]
        }
    };
    
    let lc : LanguageClient = new LanguageClient('intraj','intraj', serverOptions, clientOptions);
    lc.start();
}

