create table vote (
  id integer identity primary key,
  election_Id integer not null,
  voter_Id integer not null,
  candidate_Id integer not null
);
