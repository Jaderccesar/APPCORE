

const API_BASE = "http://localhost:8080";  
const postsContainer = document.getElementById("posts-container");
const formCreatePost = document.getElementById("form-create-post");

// Chamada inicial: exige login e carrega feed
document.addEventListener("DOMContentLoaded", () => {

    const type = getUserType();

    document.querySelectorAll("[data-show]").forEach(el => {
        const allowed = el.dataset.show.split(",");
        if (!allowed.includes(type) && !allowed.includes("ALL")) {
            el.style.display = "none";
        }
    });
   
    requireUser();
    loadFeed();
});


async function loadFeed() {
    postsContainer.innerHTML = "<p>Carregando posts...</p>";

    try {
        const response = await fetch(`${API_BASE}/posts`);
        if (!response.ok) {
            throw new Error("Erro ao carregar posts");
        }

        const posts = await response.json();
        renderPosts(posts);

    } catch (error) {
        console.error("Erro:", error);
        postsContainer.innerHTML = "<p>Erro ao carregar o feed.</p>";
    }
}


function renderPosts(posts) {
    if (!posts || posts.length === 0) {
        postsContainer.innerHTML = "<p>Nenhum post encontrado.</p>";
        return;
    }

    postsContainer.innerHTML = "";

    posts.forEach(post => {
        const author = post.author || {};
        
        const item = document.createElement("div");
        item.classList.add("post-card");

        item.innerHTML = `
            <div class="post-header">

                <div>
                    <strong>${author.name ?? "Autor desconhecido"}</strong>

                    <div class="post-meta">
                        ${new Date(post.createDate).toLocaleString()}
                        • <span class="status-${post.status}">${post.status}</span>
                    </div>

                    <div class="post-author-extra">
                        <small>${author.accountType ?? "-"}</small><br>
                        ${author.specializedArea ? `<small>Área: ${author.specializedArea}</small>` : ""}
                    </div>

                </div>
            </div>

            <h2 class="post-title">${post.title}</h2>

            <p class="post-content">${post.content}</p>
        `;

        postsContainer.appendChild(item);
    });
}

const modal = document.getElementById("modal-overlay");
const titleInput = document.getElementById("modal-title-input");
const contentInput = document.getElementById("modal-content-input");

// Abrir modal (botão “Novo Post” no seu layout)
document.getElementById("btn-open-modal").onclick = () => {
    modal.style.display = "flex";
};
 
// Fechar modal
function closePostModal() {
    modal.style.display = "none";
}

async function publishPostModal() {
    const title = titleInput.value.trim();
    const content = contentInput.value.trim();
    const userId = getUserId();
    let userType = getUserType();

    if (userType === 'PROFESSOR') {
        userType  = 'TEACHER';
    } else if (userType === 'ESTUDANTE') {
        userType = 'STUDENT'; 
    }

    if (!title || !content) {
        alert("Preencha título e conteúdo!");
        return;
    }

    const response = await fetch("http://localhost:8080/posts", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            title,
            content,
            status: "ATIVA",
            author: {
                type: userType,
                id: userId 
            }
        })
    });

    if (!response.ok) {
        alert("Erro ao publicar: " + response.status);
        return;
    }

    closePostModal();
    titleInput.value = "";
    contentInput.value = "";

    loadFeed();
}

// Fecha modal clicando fora
modal.onclick = (e) => {
    if (e.target === modal) {
        closePostModal();
    }
};