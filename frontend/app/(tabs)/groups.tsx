import { View, Text } from "react-native";

export default function GroupsScreen() {
  return (
    <View className="flex-1 items-center justify-center">
      <Text className="text-lg font-bold">모임</Text>
      <Text className="mt-2 text-gray-500">모임 목록이 여기에 표시됩니다.</Text>
    </View>
  );
}
