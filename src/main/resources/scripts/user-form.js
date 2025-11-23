const accountTypeSelect = document.getElementById("accountType");
const teacherFields = document.getElementById("teacherFields");

// Mostrar campos específicos
accountTypeSelect.addEventListener("change", () => {
    const type = accountTypeSelect.value;

    teacherFields.classList.add("d-none");

    if (type === "TEACHER") {
        teacherFields.classList.remove("d-none");
    }
}); 

// Envio do formulário
document.getElementById("userForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const accountType = accountTypeSelect.value;

    const payload = {
        name: document.getElementById("name").value,
        email: document.getElementById("email").value,
        password: document.getElementById("password").value,
        cpf: document.getElementById("cpf").value,
        birthday: document.getElementById("birthday").value,
        genero: document.getElementById("genero").value,
        accountType: accountType
    };

    if (accountType === "TEACHER") {
        payload.specializedArea = document.getElementById("specializedArea").value;
    }

    try {
        const response = await fetch("http://localhost:8080/users", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });

        if (!response.ok) throw new Error("Erro ao salvar");

        const data = await response.json();
        alert("Usuário criado com sucesso!");

    } catch (err) {
        alert("Erro ao criar usuário");
        console.log(err);
    }
});