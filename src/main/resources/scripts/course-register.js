document.querySelector('.course-form').addEventListener('submit', async (e) => {
        e.preventDefault();

        const data = {
            title: document.getElementById('title').value,
            description: document.getElementById('description').value,
            level: document.getElementById('level').value,
            workload: parseInt(document.getElementById('workload').value),
            price: parseFloat(document.getElementById('price').value),
            status: document.getElementById('status').value.toUpperCase(),
            imageUrl: document.getElementById('image_url').value,
            rating: parseFloat(document.querySelector('input[name="rating"]:checked')?.value || 0),
            certificateEnabled: document.getElementById('certificate_enabled').checked
        };

        try {
            const response = await fetch("http://localhost:8080/courses/save", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(data)
            });

            if (response.ok) {
                alert("Curso criado com sucesso!");
                document.querySelector('.course-form').reset();
            } else {
                const err = await response.text();
                alert("Erro ao criar curso: " + err);
            }
        } catch (error) {
            console.error("Erro na requisição:", error);
            alert("Falha ao conectar com o servidor.");
        }
    });