BEGIN;

ALTER TABLE public.emails
DROP CONSTRAINT fk_emails_user;

ALTER TABLE public.emails
    RENAME COLUMN "userId" TO "userContextId";

CREATE INDEX "idx_emails_userContextId"
    ON public.emails ("userContextId");

ALTER TABLE public.emails
    ADD CONSTRAINT "fk_emails_userContextId"
        FOREIGN KEY ("userContextId")
            REFERENCES public.user_contexts (id)
            ON DELETE CASCADE;

COMMIT;
