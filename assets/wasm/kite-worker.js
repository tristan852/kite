importScripts("./kite.wasm-runtime.js");

let teavm;
let exports;

async function init() {
    if (!teavm) {
        teavm = await TeaVM.wasmGC.load(
            new URL("./kite.wasm", self.location.href).href
        );

        exports = teavm.exports;
    }

    return exports;
}

self.onmessage = async ({ data }) => {
    const { id, method, args = [] } = data;

    try {
        const api = await init();

        const fn = api[method];

        if (typeof fn !== "function") {
            throw new Error(`Unknown method: ${method}`);
        }

        const result = fn(...args);

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
