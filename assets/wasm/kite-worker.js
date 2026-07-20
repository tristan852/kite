import "./kite.wasm-runtime.js";

let exportsPromise;

/**
 * Loads the TeaVM runtime once.
 */
function getExports() {
    if (!exportsPromise) {
        exportsPromise = TeaVM.wasmGC
            .load(new URL("./kite.wasm", import.meta.url))
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
            throw new Error(`Unknown API method '${method}'.`);
        }

        const result = await fn(...args);

        self.postMessage({
            id,
            result
        });
    } catch (err) {
        self.postMessage({
            id,
            error: err instanceof Error ? err.message : String(err)
        });
    }
};
