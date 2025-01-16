import QUser from "./QUser";

class UserDto {
  id: string;
  name: string;
  email: string;
  resetDay: number;
  language: string;
  messagingToken: string;
  hasPhoto: boolean;

  constructor(
    id: string = "",
    name: string = "",
    email: string = "",
    resetDay: number = 1,
    language: string = "",
    messagingToken: string = "",
    hasPhoto: boolean = false,
  ) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.resetDay = resetDay;
    this.language = language;
    this.messagingToken = messagingToken;
    this.hasPhoto = hasPhoto;
  }
}

export const userDtoToDomain = (dto: UserDto) => {
  return new QUser(
    dto.id,
    dto.name,
    dto.email,
    dto.resetDay,
    "",
    dto.language,
    dto.messagingToken,
    dto.hasPhoto,
  );
}

export const userDtoToMap = (dto: UserDto, uid: string | null) => {
    return  {
      "id": uid ?? dto.id,
      "name": dto.name,
      "email": dto.email,
      "resetDay": dto.resetDay,
      "language": dto.language,
      "messagingToken": dto.messagingToken,
      "hasPhoto": dto.hasPhoto,
    };
}

export default UserDto;
