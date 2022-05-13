'use strict';
Object.defineProperty(exports, "__esModule", { value: true });
exports.activate = void 0;
const path = require("path");
const vscode_1 = require("vscode");
const vscode_languageclient_1 = require("vscode-languageclient");
function activate(context) {
    let script = 'java';
    let args = ['-jar', context.asAbsolutePath(path.join('intraj.jar')), '-vscode'];
    let serverOptions = {
        run: { command: script, args: args },
        debug: { command: script, args: args } //,ni options: { env: createDebugEnv() }
    };
    //   Use this for debugging 
    // let serverOptions = () => {
    // 	const socket = net.connect({ port: 5007 })
    // 	const result: StreamInfo = {
    // 		writer: socket,
    // 		reader: socket
    // 	}
    // 	return new Promise<StreamInfo>((resolve) => {
    // 		socket.on("connect", () => resolve(result))
    // 		socket.on("error", _ =>
    // 			window.showErrorMessage(
    // 				"Failed to connect to TaintBench language server. Make sure that the language server is running " +
    // 				"-or- configure the extension to connect via standard IO."))
    // 	})
    // }
    let clientOptions = {
        documentSelector: [{ scheme: 'file', language: 'java' }],
        synchronize: {
            configurationSection: 'java',
            fileEvents: [vscode_1.workspace.createFileSystemWatcher('**/*.java')]
        }
    };
    // Create the language client and start the client.
    let lc = new vscode_languageclient_1.LanguageClient('intraj', 'intraj', serverOptions, clientOptions);
    lc.start();
}
exports.activate = activate;
//# sourceMappingURL=extension.js.map