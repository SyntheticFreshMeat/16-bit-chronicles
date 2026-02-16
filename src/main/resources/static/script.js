const locationName = document.getElementById("location-name");
const locationDesc = document.getElementById("location-desc");
const locationImage = document.getElementById("location-image");

const playerName = document.getElementById("player-name");
const playerLevel = document.getElementById("player-level");
const playerHealth = document.getElementById("player-health");
const playerExp = document.getElementById("player-exp");
const decisionsDiv = document.getElementById("decisions");
const messageP = document.getElementById("message");

// Backpack UI elements
const backpackBtn = document.getElementById("backpackBtn");
const backpackOverlay = document.getElementById("backpackOverlay");
const closeBackpackBtn = document.getElementById("closeBackpackBtn");
const backpackSlots = document.getElementById("backpackSlots");
const backpackGrid = document.getElementById("backpackGrid");
const backpackList = document.getElementById("backpackList");

// Game Over UI elements
const gameOverOverlay = document.getElementById("gameOverOverlay");
const retryBtn = document.getElementById("retryBtn");

// New Game UI elements
const newGameOverlay = document.getElementById("newGameOverlay");
const playerNameInput = document.getElementById("playerNameInput");
const startBtn = document.getElementById("startBtn");
const saveBtn = document.getElementById("saveBtn");
const loadBtn = document.getElementById("loadBtn");

let player, currentLocation, decisions;
let message = "";
let gameOver = false;

// ---------------- NEW GAME ----------------
if (startBtn) {
    startBtn.addEventListener("click", async () => {
        const name = (playerNameInput?.value || "").trim();

        if (!name) {
            message = "Debes poner un nombre. Las partidas se guardan con el nombre del personaje.";
            updateScreen();
            return;
        }

        try {
            const res = await fetch("/start", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ name }),
            });

            if (!res.ok) {
                const txt = await res.text();
                console.error("ERROR /start:", res.status, txt);
                message = `Error al empezar (${res.status}). Mira consola.`;
                updateScreen();
                return;
            }

            const data = await res.json();
            player = data.player;
            currentLocation = data.location;
            decisions = data.decisions || [];
            message = data.message || "";
            gameOver = !!data.gameOver;

            newGameOverlay?.classList.add("hidden");
            updateScreen();
        } catch (err) {
            console.error("Fallo en fetch /start:", err);
            message = "No puedo conectar con el servidor (/start).";
            updateScreen();
        }
    });
}

//---------------- SAVE / LOAD ----------------
if (saveBtn) {
    saveBtn.addEventListener("click", async () => {
        const res = await fetch("/save", {method: "POST"});

        if (res.ok) {
            message = "Partida guardada";
        } else {
            message = "Error al guardar la partida";
        }
        updateScreen();
    });
}

if (loadBtn) {
    loadBtn.addEventListener("click", async() => {
        const res = await fetch("/load", { method: "POST"});
        const data = await res.json();

        player = data.player;
        currentLocation = data.location;
        decisions = data.decisions || [];
        message = data.message || "";
        gameOver = !!data.gameOver;

        updateScreen();
    });
}
// --------------- BACKPACK -----------------
if (backpackBtn) backpackBtn.addEventListener("click", () => openBackpack());
if (closeBackpackBtn) closeBackpackBtn.addEventListener("click", () => closeBackpack());

// cerrar al clicar fuera del modal
if (backpackOverlay) {
    backpackOverlay.addEventListener("click", (e) => {
        if (e.target === backpackOverlay) closeBackpack();
    });
}

// cerrar con ESC
document.addEventListener("keydown", (e) => {
    if (e.key === "Escape") {
        closeBackpack();
    }
});

function openBackpack() {
    if (gameOver) return;
    backpackOverlay?.classList.remove("hidden");
    backpackOverlay?.setAttribute("aria-hidden", "false");
}

function closeBackpack() {
    backpackOverlay?.classList.add("hidden");
    backpackOverlay?.setAttribute("aria-hidden", "true");
}

// --------------- GAME OVER ----------------
if (retryBtn) {
    retryBtn.addEventListener("click", async () => {
        const res = await fetch("/reset", { method: "POST" });
        const data = await res.json();

        player = data.player;
        currentLocation = data.location;
        decisions = data.decisions || [];
        message = data.message || "";
        gameOver = !!data.gameOver;

        closeBackpack();
        updateScreen();
    });
}

function openGameOver() {
    gameOverOverlay?.classList.remove("hidden");
    gameOverOverlay?.setAttribute("aria-hidden", "false");
}

function closeGameOver() {
    gameOverOverlay?.classList.add("hidden");
    gameOverOverlay?.setAttribute("aria-hidden", "true");
}

// ---------------- USE ITEM ----------------
async function useItem(itemId) {
    if (gameOver) return;

    const res = await fetch("/use-item", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ itemId }),
    });

    const data = await res.json();

    player = data.player;
    currentLocation = data.location;
    decisions = data.decisions || [];
    message = data.message || "";
    gameOver = !!data.gameOver;

    closeBackpack();
    updateScreen();
}

// -------------- RENDER BACKPACK -----------
function renderBackpack(p) {
    if (!p) return;

    const capacity = p.backpackCapacity ?? 0;
    const items = p.backpack ?? [];

    // contador slots
    if (backpackSlots) backpackSlots.textContent = `${items.length}/${capacity}`;

    // grid slots
    if (!backpackGrid) return;
    backpackGrid.innerHTML = "";

    for (let i = 0; i < capacity; i++) {
        const slot = document.createElement("div");
        slot.className = "slot";

        const item = items[i];
        if (item) {
            slot.classList.add("filled");

            // clickable
            slot.style.cursor = "pointer";
            slot.title = item.name;

            slot.addEventListener("click", () => {
                if (item.id === "herbs") {
                    useItem("herbs");
                } else {
                    message = "Ese objeto no se puede usar todavía.";
                    updateScreen();
                }
            });

            const img = document.createElement("img");
            img.style.width = "100%";
            img.style.height = "100%";
            img.style.objectFit = "contain";
            img.style.imageRendering = "pixelated";

            switch (item.id) {
                case "herbs":
                    img.src = "images/Hierbas.png";
                    break;
                case "ancient_relic":
                    img.src = "images/Reliquia.png";
                    break;
                case "torn_map":
                    img.src = "images/Mapa.png";
                    break;
                default:
                    img.src = "images/Mochila.png";
            }

            slot.appendChild(img);

            const qty = document.createElement("div");
            qty.className = "qty";
            qty.textContent = item.quantity;
            slot.appendChild(qty);
        }

        backpackGrid.appendChild(slot);
    }

    // lista items
    if (!backpackList) return;
    backpackList.innerHTML = "";

    if (items.length === 0) {
        const empty = document.createElement("div");
        empty.className = "item-row";
        empty.innerHTML = `<span class="name">(Empty)</span><span class="count">—</span>`;
        backpackList.appendChild(empty);
        return;
    }

    items.forEach((it) => {
        const row = document.createElement("div");
        row.className = "item-row";
        row.innerHTML = `<span class="name">${it.name}</span><span class="count">x${it.quantity}</span>`;
        backpackList.appendChild(row);
    });
}

// -------------- UPDATE SCREEN -------------
function updateScreen() {
    if (currentLocation) {
        locationName.textContent = currentLocation.name;
        locationDesc.textContent = currentLocation.description;

        if (locationImage && currentLocation.image) {
            locationImage.src = `images/${currentLocation.image}`;
        }
    } else {
        locationName.textContent = "Ubicación desconocida";
        locationDesc.textContent = "";
        if (locationImage) locationImage.src = "";
    }

    if (player) {
        playerName.textContent = `Jugador: ${player.name}`;
        playerLevel.textContent = `Nivel: ${player.level}`;
        playerHealth.textContent = `Vida: ${player.health}`;
        playerExp.textContent = `Experiencia: ${player.experience}`;

        renderBackpack(player);
    } else {
        playerName.textContent = "";
        playerLevel.textContent = "";
        playerHealth.textContent = "";
        playerExp.textContent = "";
    }

    if (messageP) messageP.textContent = message;

    // decisiones
    decisionsDiv.innerHTML = "";

    if (gameOver) {
        closeBackpack();
        openGameOver();

        const info = document.createElement("p");
        info.textContent = "GAME OVER — no puedes tomar más decisiones.";
        decisionsDiv.appendChild(info);
        return;
    } else {
        closeGameOver();
    }

    (decisions || []).forEach((decision) => {
        const btn = document.createElement("button");
        btn.textContent = decision.text;
        btn.onclick = () => takeDecision(decision.text);
        decisionsDiv.appendChild(btn);
    });
}

// -------------- TAKE DECISION -------------
async function takeDecision(text) {
    const res = await fetch("/decision", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ text }),
    });

    const data = await res.json();
    player = data.player;
    currentLocation = data.location;
    decisions = data.decisions || [];
    message = data.message || "";
    gameOver = !!data.gameOver;

    if (gameOver) closeBackpack();
    updateScreen();
}

// No auto-load: se empieza con el overlay NEW GAME
updateScreen();
