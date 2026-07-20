export class Kite {
    #worker;
    #nextId = 0;
    #pending = new Map();

    static async create(options = {}) {
        const worker = new Worker(
            new URL("./kite-worker.js", import.meta.url),
            { type: "module" }
        );

        const kite = new Kite(worker);

        await kite.#call("initialize", options);

        return kite;
    }

    constructor(worker) {
        this.#worker = worker;

        worker.onmessage = ({ data }) => {
            const { id, result, error } = data;

            const pending = this.#pending.get(id);
            if (!pending) return;

            this.#pending.delete(id);

            if (error) {
                pending.reject(new Error(error));
            } else {
                pending.resolve(result);
            }
        };
    }

    #call(method, ...args) {
        return new Promise((resolve, reject) => {
            const id = this.#nextId++;

            this.#pending.set(id, { resolve, reject });

            this.#worker.postMessage({
                id,
                method,
                args
            });
        });
    }

    terminate() {
        this.#worker.terminate();
    }

    encrypt(data) {
        return this.#call("encrypt", data);
    }

    decrypt(data) {
        return this.#call("decrypt", data);
    }

    sign(data) {
        return this.#call("sign", data);
    }

    verify(signature, data) {
        return this.#call("verify", signature, data);
    }
}
