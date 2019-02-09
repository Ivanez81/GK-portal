package ru.geekbrains.gkportal.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.gkportal.entity.Contact;
import ru.geekbrains.gkportal.entity.questionnaire.Answer;
import ru.geekbrains.gkportal.entity.questionnaire.Questionnaire;
import ru.geekbrains.gkportal.repository.QuestionnaireContactConfirmRepository;
import ru.geekbrains.gkportal.repository.QuestionnaireRepository;

import java.util.Comparator;
import java.util.List;

@Service
public class QuestionnaireService {

    private QuestionnaireRepository questionnaireRepository;
    private QuestionnaireContactConfirmRepository questionnaireContactConfirmRepository;

    @Autowired
    public void setQuestionnaireContactConfirmRepository(QuestionnaireContactConfirmRepository questionnaireContactConfirmRepository) {
        this.questionnaireContactConfirmRepository = questionnaireContactConfirmRepository;
    }

    @Autowired
    public void setQuestionnaireRepository(QuestionnaireRepository questionnaireRepository) {
        this.questionnaireRepository = questionnaireRepository;
    }

    public Questionnaire findById(String id) {
        Questionnaire questionnaire = questionnaireRepository.findById(id).orElse(null);

        if (questionnaire != null) {
            questionnaire.getQuestions().forEach(questionnaireQuestion ->
                    questionnaireQuestion.getAnswers()
                            .sort(Comparator.comparingInt(Answer::getSortNumber)));
        }
        return questionnaire;
    }

    public Questionnaire save(Questionnaire questionnaire) {
        return questionnaireRepository.save(questionnaire);
    }

    public boolean isQuestionnaireContactExist(Questionnaire questionnaire, Contact contact) {
        return (questionnaireContactConfirmRepository.getByQuestionnaireAndAndContact(questionnaire, contact) != null);
    }



    public List<Questionnaire> findAll() {
        return questionnaireRepository.findAll();
    }
}
