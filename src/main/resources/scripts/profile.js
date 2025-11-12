function loadUserData() {
        if (!userData) {
            return;
        }

        // Dados Pessoais
        document.getElementById('inputName').value = userData.name || '';
        document.getElementById('inputCPF').value = userData.cpf || '';
        document.getElementById('inputBirthday').value = userData.birthday || '';
        // Mapear enum Gender (MALE, FEMALE, OTHER) para valores do HTML
        const generoMap = {
            'MALE': 'masculino',
            'FEMALE': 'feminino',
            'OTHER': 'outro'
        };
        document.getElementById('inputGenero').value = generoMap[userData.genero] || '';

        // Informações da Conta
        document.getElementById('inputEmail').value = userData.email || '';
        // Mapear enum AccountType (STUDENT, TEACHER, ENTERPRISE) para valores do HTML
        const accountTypeMap = {
            'STUDENT': 'aluno',
            'TEACHER': 'professor',
            'ENTERPRISE': 'empresa'
        };
        document.getElementById('inputAccountType').value = accountTypeMap[userData.accountType] || '';
        // Mapear enum FavoriteLanguage (PORTUGUESE, ENGLISH) para valores do HTML
        // Note: O HTML tem linguagens de programação, mas o enum é de idioma
        // Por enquanto, mapeamos para valores padrão ou deixamos vazio
        const languageMap = {
            'PORTUGUESE': 'javascript', // Valor padrão
            'ENGLISH': 'javascript'
        };
        // Se o enum não corresponder, deixamos o campo vazio para o usuário escolher
        document.getElementById('inputFavLanguage').value = languageMap[userData.favoriteLanguage] || '';

        // Status da conta
        const statusText = userData.status === 'ACTIVE' ? 'Ativa' : 'Inativa';
        document.getElementById('accountStatus').textContent = statusText;

        // Endereço (mapeando campos do modelo Address)
        if (userData.address) {
            document.getElementById('inputCEP').value = userData.address.zip || '';
            document.getElementById('inputRua').value = userData.address.street || '';
            document.getElementById('inputNumero').value = userData.address.number || '';
            document.getElementById('inputComplemento').value = userData.address.complement || '';
            // Note: Address model não tem bairro, mas mantemos o campo no HTML
            document.getElementById('inputBairro').value = '';
            document.getElementById('inputCidade').value = userData.address.city || '';
            // Note: Address model tem state, mas não há campo UF no HTML atual
            // Se necessário adicionar campo UF depois
        } else {
            // Limpa campos se não houver endereço
            document.getElementById('inputCEP').value = '';
            document.getElementById('inputRua').value = '';
            document.getElementById('inputNumero').value = '';
            document.getElementById('inputComplemento').value = '';
            document.getElementById('inputBairro').value = '';
            document.getElementById('inputCidade').value = '';
        }

        // Atualizar header do perfil
        document.getElementById('profileName').textContent = userData.name || 'Nome do Usuário';
        document.getElementById('profileEmail').textContent = userData.email || 'usuario@email.com';

        // Iniciais do avatar
        if (userData.name) {
            const initials = userData.name.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
            document.getElementById('avatarInitials').textContent = initials;
        }

        // Estatísticas (pode ser expandido depois com dados reais)
        // Por enquanto mantemos valores padrão ou calculados
        updateStats();
    }

    function updateStats() {
        // Estas estatísticas podem ser calculadas ou buscadas do backend depois
        // Por enquanto mantemos valores padrão
        // document.getElementById('coursesCount').textContent = userData.coursesCount || '0';
        // document.getElementById('badgesCount').textContent = userData.badgesCount || '0';
        // document.getElementById('pointsCount').textContent = userData.totalScore || '0';
    }

    function switchTab(tabName) {

        document.querySelectorAll('.tab-btn').forEach(btn => {
            btn.classList.remove('active');
        });
        document.querySelectorAll('.tab-content').forEach(content => {
            content.classList.remove('active');
        });

        document.querySelector(`[data-tab="${tabName}"]`).classList.add('active');
        document.getElementById(`tab-${tabName}`).classList.add('active');
    }

    function toggleEdit(formType) {
        const form = document.getElementById(`${formType}Form`);
        const inputs = form.querySelectorAll('.form-input');
        const actions = document.getElementById(`${formType}Actions`);
        const editBtn = document.getElementById(`editBtn${capitalizeFirst(formType)}`);

        const isDisabled = inputs[0].disabled;

        inputs.forEach(input => {
            input.disabled = !isDisabled;
        });

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

        inputs.forEach(input => {
            input.disabled = true;
        });

        actions.style.display = 'none';
        editBtn.textContent = 'Editar';
        editBtn.parentElement.classList.remove('btn-outline');

        // Recarrega os dados originais
        loadUserData();
    }

    function capitalizeFirst(str) {
        return str.charAt(0).toUpperCase() + str.slice(1);
    }

    function setupFormHandlers() {
        // Formulário de Dados Pessoais
        document.getElementById('personalForm').addEventListener('submit', async function(e) {
            e.preventDefault();

            if (!currentUserId) {
                showNotification('Erro: ID do usuário não encontrado.', 'error');
                return;
            }

            // Mapear valores do HTML para enum Gender
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

        // Formulário de Informações da Conta
        document.getElementById('accountForm').addEventListener('submit', async function(e) {
            e.preventDefault();

            if (!currentUserId) {
                showNotification('Erro: ID do usuário não encontrado.', 'error');
                return;
            }

            // Mapear valores do HTML para enums
            const accountTypeValue = document.getElementById('inputAccountType').value;
            const accountTypeEnumMap = {
                'aluno': 'STUDENT',
                'professor': 'TEACHER',
                'empresa': 'ENTERPRISE',
                'escola': 'ENTERPRISE' // escola pode mapear para ENTERPRISE ou criar enum separado
            };

            const languageValue = document.getElementById('inputFavLanguage').value;
            // Note: O enum FavoriteLanguage é de idioma (PORTUGUESE/ENGLISH), não linguagem de programação
            // Por enquanto, mantemos PORTUGUESE como padrão quando uma linguagem é selecionada
            // Isso pode ser ajustado depois se o modelo mudar
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

        // Formulário de Endereço
        document.getElementById('addressForm').addEventListener('submit', async function(e) {
            e.preventDefault();

            if (!currentUserId) {
                showNotification('Erro: ID do usuário não encontrado.', 'error');
                return;
            }

            const addressData = {
                street: document.getElementById('inputRua').value,
                number: parseInt(document.getElementById('inputNumero').value) || 0,
                complement: document.getElementById('inputComplemento').value,
                city: document.getElementById('inputCidade').value,
                state: '', // Campo UF não existe no HTML atual, pode ser adicionado depois
                zip: document.getElementById('inputCEP').value,
                country: 'Brasil'
            };

            const updatedData = {
                address: addressData
            };

            await saveUserData(currentUserId, updatedData, 'address');
        });
    }

    // Função para salvar dados no backend
    async function saveUserData(userId, updatedData, formType) {
        try {
            // Busca o usuário atual para preservar outros campos
            const currentResponse = await fetch(`/Students/${userId}`);
            if (!currentResponse.ok) {
                throw new Error('Erro ao buscar dados atuais do usuário');
            }

            const currentUser = await currentResponse.json();

            // Mescla os dados atualizados com os dados atuais
            const dataToUpdate = {
                ...currentUser,
                ...updatedData
            };

            const response = await fetch(`/Students/update/${userId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(dataToUpdate)
            });

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || 'Erro ao salvar dados');
            }

            const updatedUser = await response.json();
            userData = updatedUser;

            // Desabilita os campos novamente
            cancelEdit(formType);

            showNotification('Dados salvos com sucesso!', 'success');

            // Recarrega os dados para garantir sincronização
            loadUserData();

        } catch (error) {
            console.error('Erro ao salvar:', error);
            showNotification('Erro ao salvar dados: ' + error.message, 'error');
        }
    }

    function changeAvatar() {
        document.getElementById('avatarInput').click();
    }

    function handleAvatarChange(event) {
        const file = event.target.files[0];
        if (file && file.type.startsWith('image/')) {
            const reader = new FileReader();
            reader.onload = function(e) {
                const avatar = document.getElementById('profileAvatar');
                avatar.style.backgroundImage = `url(${e.target.result})`;
                avatar.style.backgroundSize = 'cover';
                avatar.style.backgroundPosition = 'center';
                document.getElementById('avatarInitials').style.display = 'none';

                showNotification('Foto de perfil atualizada!', 'success');
            };
            reader.readAsDataURL(file);
        }
    }

    function handleLogout() {
        if (confirm('Tem certeza que deseja sair?')) {
            // Limpa a sessão no servidor
            fetch('/logout', { method: 'POST' })
                .then(() => {
                    showNotification('Até logo!', 'info');
                    setTimeout(() => {
                        window.location.href = '/';
                    }, 1500);
                })
                .catch(() => {
                    // Mesmo se der erro, redireciona
                    window.location.href = '/';
                });
        }
    }

    // Função para exibir notificações
    function showNotification(message, type = 'info') {
        // Cria elemento de notificação se não existir
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

        // Define cor baseado no tipo
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

        // Remove após 3 segundos
        setTimeout(() => {
            notification.style.opacity = '0';
            setTimeout(() => {
                notification.style.display = 'none';
            }, 300);
        }, 3000);
    }

    // Funções para máscaras de CPF e CEP
    function setupCPFMask() {
        const cpfInput = document.getElementById('inputCPF');
        if (cpfInput) {
            cpfInput.addEventListener('input', function(e) {
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
            cepInput.addEventListener('input', function(e) {
                let value = e.target.value.replace(/\D/g, '');
                if (value.length <= 8) {
                    value = value.replace(/(\d{5})(\d)/, '$1-$2');
                    e.target.value = value;
                }
            });
        }
    }