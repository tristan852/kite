importScripts("./kite.wasm-runtime.js");

let exportsPromise;

function getExports() {
    if (!exportsPromise) {
        exportsPromise = TeaVM.wasmGC
            .load(new URL("./kite.wasm", self.location.href).href)
            .then(teavm => teavm.exports);
    }

    return exportsPromise;
}

self.onmessage = async ({ data }) => {
    const { id, method, args = [] } = data;

    try {
        const exports = await getExports();

        const fn = exports[method];

        if (typeof fn !== "function") {
            throw new Error(`Unknown method '${method}'`);
        }

        const result = await fn(...args);

        self.postMessage({
            id,
            result
        });
    } catch (error) {
        self.postMessage({
            id,
            error: error instanceof Error
                ? error.message
                : String(error)
        });
    }
};
