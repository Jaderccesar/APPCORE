let currentUserId = null;
let userData = null;
const typeMap = {
    "ESTUDANTE": "STUDENT",
    "PROFESSOR": "TEACHER",
    "EMPRESA": "ENTERPRISE"
};

document.addEventListener("DOMContentLoaded", async () => {
    const currentUserId = requireUser();
    const type = getUserType();

    console.log("User ID:", currentUserId, "Type:", type);
    const typeMap = {
        "ESTUDANTE": "STUDENT",
        "PROFESSOR": "TEACHER",
        "EMPRESA": "ENTERPRISE"
    };

    const normalizedType = typeMap[type] || type;

    if (!currentUserId) {
        alert("Erro: usuário não identificado.");
        window.location.href = "home.html";
        return;
    }

    if (!type) {
        alert("Erro: tipo de usuário não encontrado.");
        window.location.href = "home.html";
        return;
    }

    console.log("Type: " + type  );
    document.querySelectorAll("[data-show]").forEach(el => {
        const allowed = el.dataset.show.split(",");
        if (!allowed.includes(type) && !allowed.includes("ALL")) {
            el.style.display = "none";
        }
    });

    carregarDados(currentUserId, normalizedType);

    setupFormHandlers();
    setupCPFMask();
    setupCEPMask();
});

async function carregarDados(userId, type) {
    const endpoints = {
        STUDENT: `http://localhost:8080/Students/${userId}`,
        TEACHER: `http://localhost:8080/Teachers/${userId}`,
        ENTERPRISE: `http://localhost:8080/Enterprises/${userId}`
    };

    const url = endpoints[type];

    if (!url) {
        console.error("Tipo não reconhecido:", type);
        showNotification("Erro interno: tipo de usuário inválido.", "error");
        return;
    }

    try {
        const response = await fetch(url);

        if (!response.ok) {
            throw new Error("Erro ao carregar dados do usuário");
        }

        userData = await response.json();
        loadUserData();

    } catch (err) {
        console.error("Erro ao carregar dados:", err);
        showNotification("Erro ao carregar informações do perfil.", "error");
    }
}

function loadUserData() {
    if (!userData) return;

    document.getElementById('inputName').value = userData.name || '';
    document.getElementById('inputCPF').value = userData.cpf || '';
    document.getElementById('inputBirthday').value = userData.birthday || '';

    const generoMap = {
        'MALE': 'masculino',
        'FEMALE': 'feminino',
        'OTHER': 'outro'
    };
    document.getElementById('inputGenero').value = generoMap[userData.genero] || '';

    document.getElementById('inputEmail').value = userData.email || '';

    const accountTypeMap = {
        'ESTUDANTE': 'aluno',
        'PROFESSOR': 'professor',
        'EMPRESA': 'empresa'
    };
    document.getElementById('inputAccountType').value = accountTypeMap[userData.accountType] || '';

    const languageMap = {
        'PORTUGUESE': 'javascript',
        'ENGLISH': 'javascript'
    };
    document.getElementById('inputFavLanguage').value = languageMap[userData.favoriteLanguage] || '';

    const statusText = userData.status === 'ACTIVE' ? 'Ativa' : 'Inativa';
    document.getElementById('accountStatus').textContent = statusText;

    document.getElementById('profileName').textContent = userData.name || 'Nome do Usuário';
    document.getElementById('profileEmail').textContent = userData.email || '';

    const avatar = document.getElementById('profileAvatar');
    const initialsSpan = document.getElementById('avatarInitials');

    if (userData.avatarUrl) {

        const fullUrl = `http://localhost:8080${userData.avatarUrl}`;

        avatar.style.backgroundImage = `url(${fullUrl})`;
        avatar.style.backgroundSize = 'cover';
        avatar.style.backgroundPosition = 'center';
        initialsSpan.style.display = 'none';

    } else if (userData.name) {

        const initials = userData.name.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
        initialsSpan.textContent = initials;
        initialsSpan.style.display = 'block';
        avatar.style.backgroundImage = 'none';
    } else {

        initialsSpan.textContent = 'UN';
        initialsSpan.style.display = 'block';
        avatar.style.backgroundImage = 'none';
    }

    updateStats();
}

function updateStats() {
}

function switchTab(tabName) {
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));

    document.querySelector(`[data-tab="${tabName}"]`).classList.add('active');
    document.getElementById(`tab-${tabName}`).classList.add('active');
}

function toggleEdit(formType) {
    const form = document.getElementById(`${formType}Form`);
    const inputs = form.querySelectorAll('.form-input');
    const actions = document.getElementById(`${formType}Actions`);
    const editBtn = document.getElementById(`editBtn${capitalizeFirst(formType)}`);

    const isDisabled = inputs[0].disabled;

    inputs.forEach(input => input.disabled = !isDisabled);

    if (isDisabled) {
        actions.style.display = 'flex';
        editBtn.textContent = 'Cancelar';
        editBtn.parentElement.classList.add('btn-outline');
    } else {
        actions.style.display = 'none';
        editBtn.textContent = 'Editar';
        editBtn.parentElement.classList.remove('btn-outline');
    }
}

function cancelEdit(formType) {
    const form = document.getElementById(`${formType}Form`);
    const inputs = form.querySelectorAll('.form-input');
    const actions = document.getElementById(`${formType}Actions`);
    const editBtn = document.getElementById(`editBtn${capitalizeFirst(formType)}`);

    inputs.forEach(input => input.disabled = true);

    actions.style.display = 'none';
    editBtn.textContent = 'Editar';
    editBtn.parentElement.classList.remove('btn-outline');

    loadUserData();
}

function capitalizeFirst(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
}

function setupFormHandlers() {

    document.getElementById('personalForm').addEventListener('submit', async function (e) {
        e.preventDefault();

        if (!currentUserId) return showNotification('Erro: ID do usuário não encontrado.', 'error');

        const generoValue = document.getElementById('inputGenero').value;
        const generoEnumMap = {
            'masculino': 'MALE',
            'feminino': 'FEMALE',
            'outro': 'OTHER'
        };

        const updatedData = {
            name: document.getElementById('inputName').value,
            cpf: document.getElementById('inputCPF').value,
            birthday: document.getElementById('inputBirthday').value,
            genero: generoEnumMap[generoValue] || generoValue.toUpperCase()
        };

        await saveUserData(currentUserId, updatedData, 'personal');
    });

    document.getElementById('accountForm').addEventListener('submit', async function (e) {
        e.preventDefault();

        const accountTypeValue = document.getElementById('inputAccountType').value;
        const accountTypeEnumMap = {
            'aluno': 'STUDENT',
            'professor': 'TEACHER',
            'empresa': 'ENTERPRISE',
            'escola': 'ENTERPRISE'
        };

        const languageValue = document.getElementById('inputFavLanguage').value;
        const languageEnumMap = {
            'javascript': 'PORTUGUESE',
            'python': 'PORTUGUESE',
            'java': 'PORTUGUESE',
            'csharp': 'PORTUGUESE',
            'php': 'PORTUGUESE',
            'ruby': 'PORTUGUESE',
            'go': 'PORTUGUESE',
            'rust': 'PORTUGUESE',
            'portuguese': 'PORTUGUESE',
            'english': 'ENGLISH'
        };

        const updatedData = {
            email: document.getElementById('inputEmail').value,
            accountType: accountTypeEnumMap[accountTypeValue] || accountTypeValue.toUpperCase(),
            favoriteLanguage: languageValue ? (languageEnumMap[languageValue] || 'PORTUGUESE') : userData.favoriteLanguage
        };

        await saveUserData(currentUserId, updatedData, 'account');
    });
}

async function saveUserData(userId, updatedData, formType) {
    try {
        const currentResponse = await fetch(`http://localhost:8080/Students/${userId}`);
        if (!currentResponse.ok) throw new Error('Erro ao buscar dados atuais do usuário');

        const currentUser = await currentResponse.json();

        const dataToUpdate = {
            ...currentUser,
            ...updatedData
        };

        const response = await fetch(`http://localhost:8080/Students/update/${userId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dataToUpdate)
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Erro ao salvar dados');
        }

        const updatedUser = await response.json();
        userData = updatedUser;

        cancelEdit(formType);

        showNotification('Dados salvos com sucesso!', 'success');

        loadUserData();

    } catch (error) {
        console.error('Erro ao salvar:', error);
        showNotification('Erro ao salvar dados: ' + error.message, 'error');
    }
}

function changeAvatar() {
    document.getElementById('avatarInput').click();
}

async function handleAvatarChange(event) {
    const file = event.target.files[0];
    const type = getUserType();
    const currentUserId = requireUser();

    if (!file || !currentUserId || !type) {
        showNotification('Erro: Falta o arquivo ou os dados do usuário.', 'error');
        console.log(file);
        console.log(currentUserId);
        console.log(type);
        return;
    }

    if (file.type.startsWith('image/')) {
        const reader = new FileReader();
        reader.onload = function (e) {
            const avatar = document.getElementById('profileAvatar');
            avatar.style.backgroundImage = `url(${e.target.result})`;
            avatar.style.backgroundSize = 'cover';
            avatar.style.backgroundPosition = 'center';
            document.getElementById('avatarInitials').style.display = 'none';
        };
        reader.readAsDataURL(file);
    }

    const formData = new FormData();
    formData.append('file', file);

    const normalizedType = typeMap[type] || type;

    try {

        const response = await fetch(`http://localhost:8080/users/uploadAvatar/${currentUserId}/${normalizedType}`, {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Falha ao enviar a imagem.');
        }

        const data = await response.json();

        userData.avatarUrl = data.avatarUrl;

        showNotification('Foto de perfil atualizada e salva!', 'success');

    } catch (error) {
        console.error('Erro no upload da foto:', error);
        showNotification('Erro ao salvar a foto de perfil: ' + error.message, 'error');
    }
}

function handleLogout() {
    if (confirm('Tem certeza que deseja sair?')) {
        fetch('/logout', { method: 'POST' })
            .then(() => {
                showNotification('Até logo!', 'info');
                setTimeout(() => {
                    window.location.href = 'login.html';
                }, 1500);
            })
            .catch(() => {
                window.location.href = 'login.html';
            });
    }
}

function showNotification(message, type = 'info') {
    let notification = document.getElementById('notification');
    if (!notification) {
        notification = document.createElement('div');
        notification.id = 'notification';
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 15px 20px;
            border-radius: 8px;
            color: white;
            font-weight: 500;
            z-index: 10000;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
            transition: opacity 0.3s;
        `;
        document.body.appendChild(notification);
    }

    const colors = {
        success: '#10b981',
        error: '#ef4444',
        info: '#3b82f6',
        warning: '#f59e0b'
    };
    notification.style.backgroundColor = colors[type] || colors.info;
    notification.textContent = message;
    notification.style.opacity = '1';
    notification.style.display = 'block';

    setTimeout(() => {
        notification.style.opacity = '0';
        setTimeout(() => {
            notification.style.display = 'none';
        }, 300);
    }, 3000);
}

function setupCPFMask() {
    const cpfInput = document.getElementById('inputCPF');
    if (cpfInput) {
        cpfInput.addEventListener('input', function (e) {
            let value = e.target.value.replace(/\D/g, '');
            if (value.length <= 11) {
                value = value.replace(/(\d{3})(\d)/, '$1.$2');
                value = value.replace(/(\d{3})(\d)/, '$1.$2');
                value = value.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
                e.target.value = value;
            }
        });
    }
}

function setupCEPMask() {
    const cepInput = document.getElementById('inputCEP');
    if (cepInput) {
        cepInput.addEventListener('input', function (e) {
            let value = e.target.value.replace(/\D/g, '');
            if (value.length <= 8) {
                value = value.replace(/(\d{5})(\d)/, '$1-$2');
                e.target.value = value;
            }
        });
    }
}
