document.addEventListener('DOMContentLoaded', function () {
    const deleteForms = document.querySelectorAll('form[action="/vacancies/delete-vacancy"]');

    deleteForms.forEach(form => {
        form.addEventListener('submit', function (event) {
            if (!confirm('Вы уверены, что хотите удалить эту вакансию?')) {
                event.preventDefault(); // Отменить отправку формы, если пользователь нажал "Отмена"
            }
        });
    });

    const vacancyForm = document.getElementById('vacancy-form');
    if (vacancyForm) {
        vacancyForm.addEventListener('submit', function () {
            alert('Вакансия успешно добавлена!');
        });
    }

    const editVacancyForm = document.getElementById('edit-vacancy-form');
    if (editVacancyForm) {
        editVacancyForm.addEventListener('submit', function () {
            alert('Изменения сохранены успешно!');
        });
    }
});